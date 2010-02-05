package com.example.service;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseRepository<T>
{
    private Class<T> clazz;
    private PersistenceManager pm;

    public BaseRepository(Class<T> clazz, PersistenceManager pm)
    {
        this.clazz = clazz;
        this.pm = pm;
    }

    public Collection<T> list()
    {
        List<T> entities = new ArrayList<T>();
        Extent<T> extent = pm.getExtent(clazz, false);
        for (T entity : extent)
        {
            entities.add(entity);
        }
        extent.closeAll();

        return entities;
    }

    public void create(T entity)
    {
        pm.makePersistent(entity);
    }

    public void delete(Long id)
    {
        pm.deletePersistent(pm.getObjectById(clazz, id));
    }
}