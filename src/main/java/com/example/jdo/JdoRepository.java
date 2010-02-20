package com.example.jdo;

import com.example.Repository;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

/**
 * This base class implements the full Repository interface for managing persistent JDO entities.
 *
 * @param <T> the persistent entity type
 */
public abstract class JdoRepository<T> implements Repository<T>
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

    public T get(Object key)
    {
        PersistenceManager pm = pmProvider.get();
        try
        {
            return pm.getObjectById(clazz, key);
        }
        catch (RuntimeException e)
        {
            return null;
        }
    }

    public void persist(T entity)
    {
        pmProvider.get().makePersistent(entity);
    }

    public void delete(T entity)
    {
        pmProvider.get().deletePersistent(entity);
    }

    public void runInTransaction(Runnable block)
    {
        Transaction tx = pmProvider.get().currentTransaction();
        try
        {
            tx.begin();
            block.run();
            tx.commit();
        }
        catch (RuntimeException e)
        {
            tx.rollback();
        }
    }
}