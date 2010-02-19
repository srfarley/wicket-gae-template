package com.example.jdo;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.jdo.PersistenceManager;

public abstract class JdoRepository<T>
{
    private Class<T> clazz;
    private Provider<PersistenceManager> pmProvider;

    protected JdoRepository(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    protected JdoRepository(Class<T> clazz, Provider<PersistenceManager> pmProvider)
    {
        this(clazz);
        setPersistenceManagerProvider(pmProvider);
    }

    @Inject
    protected void setPersistenceManagerProvider(Provider<PersistenceManager> pmProvider)
    {
        this.pmProvider = pmProvider;
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
}