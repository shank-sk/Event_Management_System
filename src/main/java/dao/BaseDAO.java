package dao;

import java.util.List;

import util.Hibernate;

public abstract class BaseDAO<T> {
  private final Class<T> entityClass;

  protected BaseDAO(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  protected T save(T entity) {
    return Hibernate.executeTransaction(session -> {
      session.saveOrUpdate(entity);
      return entity;
    });
  }

  protected T findById(Long id) {
    return Hibernate.executeSession(session -> session.get(entityClass, id));
  }

  protected List<T> findAll() {
    return Hibernate
        .executeSession(session -> session.createQuery("from " + entityClass.getSimpleName(), entityClass).list());
  }

  protected T update(T entity) {
    return Hibernate.executeTransaction(session -> session.merge(entity));
  }

  protected boolean delete(Long id) {
    return Hibernate.executeTransaction(session -> {
      T entity = session.get(entityClass, id);
      if (entity == null) {
        return false;
      }
      session.remove(entity);
      return true;
    });
  }
}