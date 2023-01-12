package com.panosen.android.orm;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

public class DalClientFactory {

    private final static String TAG = "DalClientFactory";

    private static final ConcurrentHashMap<String, Class<? extends SQLiteOpenHelper>> dalClientMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static SQLiteOpenHelper getClient(Context context, String dataSourceName) {
        if (!dalClientMap.containsKey(dataSourceName)) {
            return null;
        }

        Class<? extends SQLiteOpenHelper> clazz = dalClientMap.get(dataSourceName);
        if (clazz == null) {
            return null;
        }

        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(Context.class);
            return (SQLiteOpenHelper) constructor.newInstance(context);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    public static void registerDataSource(String dataSource, Class<? extends SQLiteOpenHelper> sqLiteOpenHelperClass) {
        dalClientMap.putIfAbsent(dataSource, sqLiteOpenHelperClass);
    }
}
