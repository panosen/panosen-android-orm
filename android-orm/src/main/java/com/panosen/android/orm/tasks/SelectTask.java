package com.panosen.android.orm.tasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.panosen.android.orm.ConditionsBuilders;
import com.panosen.android.orm.ContentValuesHelper;
import com.panosen.android.orm.EntityColumn;
import com.panosen.android.orm.EntityManager;
import com.panosen.codedom.sqlite.builder.ConditionsBuilder;
import com.panosen.codedom.sqlite.builder.SelectSqlBuilder;
import com.panosen.codedom.sqlite.engine.ConditionsEngine;
import com.panosen.codedom.sqlite.engine.GenerationResponse;
import com.panosen.codedom.sqlite.engine.SelectSqlEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectTask extends SingleTask {

    public SelectTask(Context context, EntityManager entityManager) {
        super(context, entityManager);
    }

    @SuppressWarnings("unchecked")
    public <TEntity> TEntity selectSingleBySample(TEntity sample) throws IllegalAccessException, InstantiationException {
        ConditionsBuilder conditionsBuilder = ConditionsBuilders.buildConditionsBuilder(entityManager, sample);

        GenerationResponse generationResponse = new ConditionsEngine().generate(conditionsBuilder);
        String whereClause = generationResponse.getSql();
        String[] whereArgs = generationResponse.getArgsAsStrings();

        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(entityManager.getTableName(), null, whereClause, whereArgs, null, null, null, "1");
        if (!cursor.moveToNext()) {
            cursor.close();
            sqLiteDatabase.close();
            return null;
        }

        Map<String, Integer> indexMap = buildIndexMap(cursor, entityManager);

        TEntity entity = buildEntity(cursor, entityManager, indexMap);

        cursor.close();
        sqLiteDatabase.close();

        return entity;
    }

    @SuppressWarnings("unchecked")
    public <TEntity> TEntity selectSingleByPrimaryKey(Object primaryKey) throws IllegalAccessException, InstantiationException {

        EntityColumn primaryKeyColumn = entityManager.getPrimaryKeyColumn();
        if (primaryKeyColumn == null) {
            return null;
        }

        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
        conditionsBuilder.equal(primaryKeyColumn.getColumnName(), primaryKey);

        GenerationResponse generationResponse = new ConditionsEngine().generate(conditionsBuilder);
        String whereClause = generationResponse.getSql();
        String[] whereArgs = generationResponse.getArgsAsStrings();

        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(entityManager.getTableName(), null, whereClause, whereArgs, null, null, null, "1");
        if (!cursor.moveToNext()) {
            cursor.close();
            sqLiteDatabase.close();
            return null;
        }

        Map<String, Integer> indexMap = buildIndexMap(cursor, entityManager);

        TEntity entity = buildEntity(cursor, entityManager, indexMap);

        cursor.close();
        sqLiteDatabase.close();

        return entity;
    }

    public <TEntity> List<TEntity> selectList() throws IllegalAccessException, InstantiationException {
        return selectListByConditions(new ConditionsBuilder());
    }

    public <TEntity> List<TEntity> selectListBySample(TEntity entity) throws IllegalAccessException, InstantiationException {
        ConditionsBuilder conditionsBuilder = ConditionsBuilders.buildConditionsBuilder(entityManager, entity);
        return selectListByConditions(conditionsBuilder);
    }

    public <TEntity> List<TEntity> selectListBySampleWithPage(TEntity entity, Integer pageIndex, Integer pageSize) throws IllegalAccessException, InstantiationException {
        ConditionsBuilder conditionsBuilder = ConditionsBuilders.buildConditionsBuilder(entityManager, entity);
        return selectListByConditionsWithPage(conditionsBuilder, pageIndex, pageSize);
    }

    public <TEntity> List<TEntity> selectListByConditions(ConditionsBuilder conditionsBuilder) throws IllegalAccessException, InstantiationException {
        return selectListByPage(conditionsBuilder, null);
    }

    public <TEntity> List<TEntity> selectListByConditionsWithPage(ConditionsBuilder conditionsBuilder, Integer pageIndex, Integer pageSize) throws IllegalAccessException, InstantiationException {
        String limit = (pageIndex * pageSize) + ", " + pageSize;
        return selectListByPage(conditionsBuilder, limit);
    }

    private <TEntity> List<TEntity> selectListByPage(ConditionsBuilder conditionsBuilder, String limit) throws IllegalAccessException, InstantiationException {
        GenerationResponse generationResponse = new ConditionsEngine().generate(conditionsBuilder);
        String whereClause = generationResponse.getSql();
        String[] whereArgs = generationResponse.getArgsAsStrings();

        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(entityManager.getTableName(), null, whereClause, whereArgs, null, null, null, limit);

        List<TEntity> entityList = new ArrayList<>();

        Map<String, Integer> indexMap = buildIndexMap(cursor, entityManager);

        while (cursor.moveToNext()) {
            TEntity entity = buildEntity(cursor, entityManager, indexMap);
            entityList.add(entity);
        }
        cursor.close();
        sqLiteDatabase.close();

        cursor.close();

        return entityList;
    }

    public int selectCount() {
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

        SelectSqlBuilder selectSqlBuilder = new SelectSqlBuilder()
                .from(entityManager.getTableName())
                .column("count(*)");

        GenerationResponse generationResponse = new SelectSqlEngine().generate(selectSqlBuilder);
        String sql = generationResponse.getSql();
        String[] args = generationResponse.getArgsAsStrings();

        Cursor cursor = sqLiteDatabase.rawQuery(sql, args);
        if (!cursor.moveToNext()) {
            cursor.close();
            sqLiteDatabase.close();
            return -1;
        }

        int count = cursor.getInt(0);

        cursor.close();
        sqLiteDatabase.close();

        return count;
    }

    public <TEntity, TId> List<TEntity> selectListByPrimaryKeys(List<TId> primaryKeys) throws IllegalAccessException, InstantiationException {
        EntityColumn primaryKeyColumn = entityManager.getPrimaryKeyColumn();
        if (primaryKeyColumn == null) {
            return null;
        }

        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
        conditionsBuilder.in(primaryKeyColumn.getColumnName(), primaryKeys);

        GenerationResponse generationResponse = new ConditionsEngine().generate(conditionsBuilder);
        String whereClause = generationResponse.getSql();
        String[] whereArgs = generationResponse.getArgsAsStrings();

        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(entityManager.getTableName(), null, whereClause, whereArgs, null, null, null);

        Map<String, Integer> indexMap = buildIndexMap(cursor, entityManager);

        List<TEntity> entityList = buildEntityList(cursor, entityManager, indexMap);

        cursor.close();
        sqLiteDatabase.close();

        return entityList;
    }

    private static Map<String, Integer> buildIndexMap(Cursor cursor, EntityManager entityManager) {
        Map<String, Integer> indexMap = new HashMap<>();
        for (Map.Entry<String, EntityColumn> entry : entityManager.getColumnMap().entrySet()) {
            if (!indexMap.containsKey(entry.getKey())) {
                indexMap.put(entry.getKey(), cursor.getColumnIndex(entry.getKey()));
            }

        }
        return indexMap;
    }

    @SuppressWarnings("unchecked")
    private static <TEntity> TEntity buildEntity(Cursor cursor, EntityManager entityManager, Map<String, Integer> indexMap) throws IllegalAccessException, InstantiationException {
        TEntity entity = (TEntity) entityManager.getClazz().newInstance();

        for (Map.Entry<String, EntityColumn> entry : entityManager.getColumnMap().entrySet()) {
            Integer index = indexMap.get(entry.getKey());
            if (index == null || index < 0) {
                continue;
            }

            ContentValuesHelper.setEntityFieldValue(cursor, entry.getValue().getField(), entity, entry.getValue().getLocalType(), index);
        }

        return entity;
    }

    private static <TEntity> List<TEntity> buildEntityList(Cursor cursor, EntityManager entityManager, Map<String, Integer> indexMap) throws IllegalAccessException, InstantiationException {
        List<TEntity> entityList = new ArrayList<>();

        while (cursor.moveToNext()) {
            TEntity entity = buildEntity(cursor, entityManager, indexMap);

            entityList.add(entity);
        }

        return entityList;
    }
}
