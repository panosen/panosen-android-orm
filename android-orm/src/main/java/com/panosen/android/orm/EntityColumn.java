package com.panosen.android.orm;

import com.panosen.android.orm.annotation.LocalType;

import java.lang.reflect.Field;

public class EntityColumn {

    private String columnName;

    private LocalType localType;

    private Field field;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public LocalType getLocalType() {
        return localType;
    }

    public void setLocalType(LocalType localType) {
        this.localType = localType;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
