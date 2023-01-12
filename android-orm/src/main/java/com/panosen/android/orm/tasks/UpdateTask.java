package com.panosen.android.orm.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.panosen.android.orm.ContentValuesHelper;
import com.panosen.android.orm.EntityColumn;
import com.panosen.android.orm.EntityManager;
import com.panosen.codedom.sqlite.builder.ConditionsBuilder;
import com.panosen.codedom.sqlite.engine.ConditionsEngine;
import com.panosen.codedom.sqlite.engine.GenerationResponse;

import java.util.List;
import java.util.Map;

public class UpdateTask extends SingleTask {

    public UpdateTask(Context context, EntityManager entityManager) {
        super(context, entityManager);
    }

    public <TEntity> int update(TEntity entity) throws IllegalAccessException {
        return update(sqLiteOpenHelper.getWritableDatabase(), entityManager, entity);
    }

    public <TEntity> int[] batchUpdate(List<TEntity> entityList) throws IllegalAccessException, SQLException {
        if (entityList == null) {
            return null;
        }

        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        IllegalAccessException illegalAccessException = null;
        SQLException sqlException = null;

        int[] totalCount = new int[entityList.size()];

        try {
            for (int index = 0, length = entityList.size(); index < length; index++) {
                totalCount[index] = update(sqLiteDatabase, entityManager, entityList.get(index));
            }

            sqLiteDatabase.setTransactionSuccessful();
        } catch (IllegalAccessException e1) {
            illegalAccessException = e1;
        } catch (SQLException e2) {
            sqlException = e2;
        }

        sqLiteDatabase.endTransaction();

        if (illegalAccessException != null) {
            throw illegalAccessException;
        }

        if (sqlException != null) {
            throw sqlException;
        }

        return totalCount;
    }

    private static <TEntity> int update(SQLiteDatabase sqLiteDatabase, EntityManager entityManager, TEntity entity) throws IllegalAccessException {

        EntityColumn primaryKeyColumn = entityManager.getPrimaryKeyColumn();
        if (primaryKeyColumn == null) {
            return 0;
        }

        ContentValues contentValues = new ContentValues();
        for (Map.Entry<String, EntityColumn> entry : entityManager.getColumnMap().entrySet()) {
            Object value = entry.getValue().getField().get(entity);
            if (value == null) {
                continue;
            }
            ContentValuesHelper.putContentValues(contentValues, entry.getKey(), value, entry.getValue().getLocalType());
        }

        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
        conditionsBuilder.equal(primaryKeyColumn.getColumnName(), primaryKeyColumn.getField().get(entity));

        GenerationResponse generationResponse = new ConditionsEngine().generate(conditionsBuilder);
        String whereClause = generationResponse.getSql();
        String[] whereArgs = generationResponse.getArgsAsStrings();

        return sqLiteDatabase.update(entityManager.getTableName(), contentValues, whereClause, whereArgs);
    }
}
