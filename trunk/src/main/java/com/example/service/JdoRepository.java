package com.example.service;

import com.example.Repository;
import com.google.inject.Provider;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class JdoRepository<T> implements Repository<T>
{
    private Class<T> clazz;
    protected Provider<PersistenceManager> pmProvider;

    protected JdoRepository(Class<T> clazz, Provider<PersistenceManager> pmProvider)
    {
        this.clazz = clazz;
        this.pmProvider = pmProvider;
    }

    public T getById(Object key)
    {
        return pmProvider.get().getObjectById(clazz, key);
    }

    public List<T> list()
    {
        List<T> entities = new ArrayList<T>();
        Extent<T> extent = pmProvider.get().getExtent(clazz, false);
        for (T entity : extent)
        {
            entities.add(entity);
        }
        extent.closeAll();
        return entities;
    }

    public void persist(T entity)
    {
        pmProvider.get().makePersistent(entity);
    }

    public void delete(Long id)
    {
        delete(pmProvider.get().getObjectById(clazz, id));
    }

    public void delete(T entity)
    {
        pmProvider.get().deletePersistent(entity);
    }

    protected List<T> list(String query, Object... parameters)
    {
        @SuppressWarnings("unchecked")
        Collection<T> results = (Collection<T>) pmProvider.get().newQuery(query).executeWithArray(parameters);
        return new ArrayList<T>(results);
    }

    protected List<T> list(String query, Map parameters)
    {
        @SuppressWarnings("unchecked")
        Collection<T> results = (Collection<T>) pmProvider.get().newQuery(query).executeWithMap(parameters);
        return new ArrayList<T>(results);
    }

    protected List<T> listUsingNamedQuery(String queryName, Object... parameters)
    {
        @SuppressWarnings("unchecked")
        Collection<T> results =
                (Collection<T>) pmProvider.get().newNamedQuery(clazz, queryName).executeWithArray(parameters);
        return new ArrayList<T>(results);
    }

    protected List<T> listUsingNamedQuery(String queryName, Map parameters)
    {
        @SuppressWarnings("unchecked")
        Collection<T> results =
                (Collection<T>) pmProvider.get().newNamedQuery(clazz, queryName).executeWithMap(parameters);
        return new ArrayList<T>(results);
    }
}