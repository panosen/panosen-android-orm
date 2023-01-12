package com.panosen.android.orm.tasks;

import android.content.Context;
import android.database.SQLException;

import com.panosen.android.orm.EntityColumn;
import com.panosen.android.orm.EntityManager;
import com.panosen.codedom.sqlite.builder.ConditionsBuilder;
import com.panosen.codedom.sqlite.engine.ConditionsEngine;
import com.panosen.codedom.sqlite.engine.GenerationResponse;

import java.util.ArrayList;
import java.util.List;

public class DeleteTask extends SingleTask {
    public DeleteTask(Context context, EntityManager entityManager) {
        super(context, entityManager);
    }

    public <TEntity> int delete(TEntity entity) throws IllegalAccessException {

        EntityColumn primaryKeyColumn = entityManager.getPrimaryKeyColumn();
        if (primaryKeyColumn == null) {
            return 0;
        }

        Object value = primaryKeyColumn.getField().get(entity);
        if (value == null) {
            return 0;
        }

        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
        conditionsBuilder.equal(primaryKeyColumn.getColumnName(), value);

        GenerationResponse generationResponse = new ConditionsEngine().generate(conditionsBuilder);
        String whereClause = generationResponse.getSql();
        String[] whereArgs = generationResponse.getArgsAsStrings();

        return sqLiteOpenHelper.getWritableDatabase().delete(entityManager.getTableName(), whereClause, whereArgs);
    }

    public <TId> int deleteByPrimaryKeys(List<TId> primaryKeys) {

        EntityColumn primaryKeyColumn = entityManager.getPrimaryKeyColumn();
        if (primaryKeyColumn == null) {
            return 0;
        }

        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
        conditionsBuilder.in(primaryKeyColumn.getColumnName(), primaryKeys);

        GenerationResponse generationResponse = new ConditionsEngine().generate(conditionsBuilder);
        String whereClause = generationResponse.getSql();
        String[] whereArgs = generationResponse.getArgsAsStrings();

        return sqLiteOpenHelper.getWritableDatabase().delete(entityManager.getTableName(), whereClause, whereArgs);
    }

    public <TEntity> int batchDelete(List<TEntity> entityList) throws IllegalAccessException, SQLException {
        if (entityList == null) {
            return 0;
        }

        EntityColumn primaryKeyColumn = entityManager.getPrimaryKeyColumn();
        if (primaryKeyColumn == null) {
            return 0;
        }

        List<Object> values = new ArrayList<>();
        for (int index = 0, length = entityList.size(); index < length; index++) {
            Object value = primaryKeyColumn.getField().get(entityList.get(index));
            if (value == null) {
                continue;
            }
            values.add(value);
        }

        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
        conditionsBuilder.in(primaryKeyColumn.getColumnName(), values);

        GenerationResponse generationResponse = new ConditionsEngine().generate(conditionsBuilder);
        String whereClause = generationResponse.getSql();
        String[] whereArgs = generationResponse.getArgsAsStrings();

        return sqLiteOpenHelper.getWritableDatabase().delete(entityManager.getTableName(), whereClause, whereArgs);
    }
}
