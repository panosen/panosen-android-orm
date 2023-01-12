package com.panosen.android.orm;

import android.content.ContentValues;
import android.database.Cursor;

import com.panosen.android.orm.annotation.LocalType;

import java.lang.reflect.Field;

public class ContentValuesHelper {

    public static void putContentValues(ContentValues parameters, String key, Object value, LocalType localType) {
        switch (localType) {
            case Long:
                parameters.put(key, (Long) value);
                break;
            case Bytes:
                parameters.put(key, (byte[]) value);
                break;
            case Float:
                parameters.put(key, (Float) value);
                break;
            case Short:
                parameters.put(key, (Short) value);
                break;
            case Double:
                parameters.put(key, (Double) value);
                break;
            case String:
                parameters.put(key, (String) value);
                break;
            case Integer:
                parameters.put(key, (Integer) value);
                break;
            default:
                break;
        }
    }

    public static void setEntityFieldValue(Cursor cursor, Field field, Object entity, LocalType localType, int index) throws IllegalAccessException {
        switch (localType) {
            case Long:
                field.set(entity, cursor.getLong(index));
                break;
            case Bytes:
                field.set(entity, cursor.getBlob(index));
                break;
            case Float:
                field.set(entity, cursor.getFloat(index));
                break;
            case Short:
                field.set(entity, cursor.getShort(index));
                break;
            case Double:
                field.set(entity, cursor.getDouble(index));
                break;
            case String:
                field.set(entity, cursor.getString(index));
                break;
            case Integer:
                field.set(entity, cursor.getInt(index));
                break;
            default:
                break;
        }
    }
}
