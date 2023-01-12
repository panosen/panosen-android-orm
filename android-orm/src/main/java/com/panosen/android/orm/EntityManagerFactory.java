package com.panosen.android.orm;

import java.util.concurrent.ConcurrentHashMap;

public class EntityManagerFactory {

    private static final ConcurrentHashMap<Class<?>, EntityManager> managers = new ConcurrentHashMap<>();

    public static <TEntity> EntityManager getOrCreateManager(Class<TEntity> clazz) {
        if (managers.containsKey(clazz)) {
            return managers.get(clazz);
        }

        synchronized (EntityManagerFactory.class) {
            if (managers.containsKey(clazz)) {
                return managers.get(clazz);
            }

            EntityManager manager = new EntityManager(clazz);
            EntityManager previous = managers.putIfAbsent(clazz, manager);

            return previous != null ? previous : manager;
        }
    }
}
