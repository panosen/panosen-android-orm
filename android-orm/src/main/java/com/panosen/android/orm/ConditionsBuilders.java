package com.panosen.android.orm;

import androidx.annotation.NonNull;

import com.panosen.codedom.sqlite.MustConditions;
import com.panosen.codedom.sqlite.builder.ConditionsBuilder;

import java.util.Map;

public class ConditionsBuilders {

    @NonNull
    public static <TEntity> ConditionsBuilder buildConditionsBuilder(EntityManager entityManager, TEntity entity) throws IllegalAccessException {
        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();

        MustConditions must = conditionsBuilder.must();

        for (Map.Entry<String, EntityColumn> entry : entityManager.getColumnMap().entrySet()) {
            Object value = entry.getValue().getField().get(entity);
            if (value == null) {
                continue;
            }
            must.equal(entry.getKey(), value);
        }

        return conditionsBuilder;
    }
}
