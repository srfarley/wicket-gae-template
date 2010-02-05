package com.example.jdo;

import com.google.inject.Binder;
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
     * Binds the JDO {@link javax.jdo.PersistenceManager} interface to the provider that allows the implementation
     * to be injected. This should be called once from within a Guice module configuration method such as
     * {@link com.google.inject.Module#configure(com.google.inject.Binder)} or
     * {@link com.google.inject.servlet.ServletModule#configureServlets()}.
     *
     * @param binder the Guice {@link com.google.inject.Binder} to use
     */
    public static void bindPersistenceManager(Binder binder)
    {
        binder.bind(PersistenceManager.class).toProvider(new Provider<PersistenceManager>()
        {
            public PersistenceManager get()
            {
                return PersistenceManagerFilter.pm.get();
            }
        });
    }
}
