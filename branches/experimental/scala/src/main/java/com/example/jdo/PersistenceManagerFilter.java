package com.example.jdo;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.*;
import java.io.IOException;

public class PersistenceManagerFilter implements Filter
{
    private PersistenceManagerFactory pmf;

    private static ThreadLocal<PersistenceManager> pm = new ThreadLocal<PersistenceManager>();

    public void init(FilterConfig filterConfig) throws ServletException
    {
        pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        try
        {
            pm.set(pmf.getPersistenceManager());
            chain.doFilter(request, response);
        }
        finally
        {
            pm.get().close();
        }
    }

    public void destroy()
    {
        pmf.close();
    }

    /**
     * This module binds the JDO {@link javax.jdo.PersistenceManager} interface to the provider that allows the
     * implementation to be injected as Provider&lt;PersistenceManager&gt;.
     */
    public static class GuiceModule extends AbstractModule
    {
        @Override
        protected void configure()
        {
            bind(PersistenceManager.class).toProvider(new Provider<PersistenceManager>()
            {
                public PersistenceManager get()
                {
                    return PersistenceManagerFilter.pm.get();
                }
            });
        }
    }
}
