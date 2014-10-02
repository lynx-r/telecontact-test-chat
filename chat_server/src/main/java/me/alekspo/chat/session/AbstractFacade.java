/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software
 * except in compliance with the terms of the license at:
 * http://developer.sun.com/berkeley_license.html
 */

package me.alekspo.chat.session;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author tgiunipero
 */
public abstract class AbstractFacade<T> {
  private Class<T> entityClass;

  public AbstractFacade(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  public void create(T entity) {
    getEntityManager().persist(entity);
  }

  protected abstract EntityManager getEntityManager();

  public void edit(T entity) {
    getEntityManager().merge(entity);
  }

  public void remove(T entity) {
    getEntityManager().remove(getEntityManager().merge(entity));
  }

  public T find(Object id) {
    return getEntityManager().find(entityClass, id);
  }

  public List<T> findAll() {
    CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
    cq.select(cq.from(entityClass));
    return getEntityManager().createQuery(cq).getResultList();
  }

  public int count() {
    CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
    Root<T> rt = cq.from(entityClass);
    cq.select(getEntityManager().getCriteriaBuilder().count(rt));
    Query q = getEntityManager().createQuery(cq);
    return ((Long) q.getSingleResult()).intValue();
  }

}