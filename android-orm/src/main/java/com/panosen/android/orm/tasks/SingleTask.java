package com.panosen.android.orm.tasks;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.panosen.android.orm.DalClientFactory;
import com.panosen.android.orm.EntityManager;

public class SingleTask {

    protected final EntityManager entityManager;

    protected final SQLiteOpenHelper sqLiteOpenHelper;

    public SingleTask(Context context, EntityManager entityManager) {
        this.entityManager = entityManager;
        sqLiteOpenHelper = DalClientFactory.getClient(context, entityManager.getDataSourceName());
    }
}
