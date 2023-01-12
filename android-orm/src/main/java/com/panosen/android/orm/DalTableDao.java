package com.panosen.android.orm;

import android.content.Context;
import android.database.SQLException;

import com.panosen.android.orm.tasks.DeleteTask;
import com.panosen.android.orm.tasks.InsertTask;
import com.panosen.android.orm.tasks.SelectTask;
import com.panosen.android.orm.tasks.UpdateTask;

import java.util.List;

public class DalTableDao<TEntity> {

    private final EntityManager entityManager;

    private final InsertTask insertTask;
    private final UpdateTask updateTask;
    private final DeleteTask deleteTask;
    private final SelectTask selectTask;

    public DalTableDao(Context context, Class<? extends TEntity> clazz) {
        this.entityManager = EntityManagerFactory.getOrCreateManager(clazz);

        this.insertTask = new InsertTask(context, entityManager);
        this.updateTask = new UpdateTask(context, entityManager);
        this.deleteTask = new DeleteTask(context, entityManager);
        this.selectTask = new SelectTask(context, entityManager);
    }

    public long insert(TEntity entity) throws IllegalAccessException {
        return this.insertTask.insert(entity);
    }

    public List<Long> batchInsert(List<TEntity> entityList) throws IllegalAccessException, SQLException {
        return this.insertTask.batchInsert(entityList);
    }

    public int update(TEntity entity) throws IllegalAccessException {
        return this.updateTask.update(entity);
    }

    public int[] batchUpdate(List<TEntity> entityList) throws IllegalAccessException {
        return this.updateTask.batchUpdate(entityList);
    }

    public int delete(TEntity entity) throws IllegalAccessException {
        return this.deleteTask.delete(entity);
    }

    public int batchDelete(List<TEntity> entityList) throws IllegalAccessException {
        return this.deleteTask.batchDelete(entityList);
    }

    public <TId> int deleteByPrimaryKeys(List<TId> primaryKeys) {
        return this.deleteTask.deleteByPrimaryKeys(primaryKeys);
    }

    public int selectCount() {
        return this.selectTask.selectCount();
    }

    public List<TEntity> selectList() throws Exception {
        return this.selectTask.selectList();
    }

    public List<TEntity> selectListBySample(TEntity entity) throws Exception {
        return this.selectTask.selectListBySample(entity);
    }

    public List<TEntity> selectListBySampleWithPage(TEntity entity, Integer pageIndex, Integer pageSize) throws IllegalAccessException, InstantiationException {
        return this.selectTask.selectListBySampleWithPage(entity, pageIndex, pageSize);
    }

    public TEntity selectSingleBySample(TEntity entity) throws Exception {
        return this.selectTask.selectSingleBySample(entity);
    }

    public TEntity selectSingleByPrimaryKey(Object id) throws Exception {
        return selectTask.selectSingleByPrimaryKey(id);
    }

    public <TId> List<TEntity> selectListByPrimaryKeys(List<TId> primaryKeys) throws Exception {
        return selectTask.selectListByPrimaryKeys(primaryKeys);
    }
}
