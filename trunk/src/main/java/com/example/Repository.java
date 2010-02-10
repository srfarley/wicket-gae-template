package com.example;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface Repository<T>
{
    List<T> list();

    void persist(T entity);

    void delete(Long id);

    void delete(T entity);
}
