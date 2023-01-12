package com.panosen.android.orm.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.panosen.android.orm.ContentValuesHelper;
import com.panosen.android.orm.EntityColumn;
import com.panosen.android.orm.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertTask extends SingleTask {

    public InsertTask(Context context, EntityManager entityManager) {
        super(context, entityManager);
    }

    public <TEntity> long insert(TEntity entity) throws IllegalAccessException {

        ContentValues contentValues = new ContentValues();
        for (Map.Entry<String, EntityColumn> entry : entityManager.getColumnMap().entrySet()) {
            Object value = entry.getValue().getField().get(entity);
            if (value == null) {
                continue;
            }
            ContentValuesHelper.putContentValues(contentValues, entry.getKey(), value, entry.getValue().getLocalType());
        }

        return sqLiteOpenHelper.getWritableDatabase().insertOrThrow(entityManager.getTableName(), null, contentValues);
    }

    public <TEntity> List<Long> batchInsert(List<TEntity> entityList) throws IllegalAccessException, SQLException {

        List<Long> ids = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        IllegalAccessException illegalAccessException = null;
        SQLException sqlException = null;

        try {
            for (TEntity entity : entityList) {
                long id = insert(entity);
                ids.add(id);
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

        return ids;
    }
}
