package com.example.web;

import com.example.jdo.PersistenceManagerFilter;
import com.google.inject.AbstractModule;

/**
 * This Guice module sets up the bindings used in this Wicket application, including the
 * JDO PersistenceManager.
 */
public class GuiceModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        // Enable per-request-thread PersistenceManager injection.
        install(new PersistenceManagerFilter.GuiceModule());

        // Business object bindings go here.
    }
}
