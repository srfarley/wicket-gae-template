package com.example;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.servlet.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class BaseTestCase
{
    protected FilteringWicketTester tester;

    public static class FilteringWicketTester extends WicketTester
    {
        private List<Filter> servletFilters;

        public FilteringWicketTester(WebApplication application, Filter... servletFilters)
        {
            super(application);
            this.servletFilters = Arrays.asList(servletFilters);
            initFilters();
        }

        @Override
        public <C extends Page> void processRequestCycle(final Class<C> pageClass, final PageParameters params)
        {
            try
            {
                doFilter(servletFilters.iterator(), new Runnable()
                {
                    public void run()
                    {
                        FilteringWicketTester.super.processRequestCycle(pageClass, params);
                    }
                });
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            catch (ServletException e)
            {
                throw new RuntimeException(e);
            }
        }

        private void doFilter(final Iterator<Filter> filterIterator,
                              final Runnable processRequestCycle) throws ServletException, IOException
        {
            if (filterIterator.hasNext())
            {
                Filter filter = filterIterator.next();
                filter.doFilter(getServletRequest(), getServletResponse(), new FilterChain()
                {
                    public void doFilter(ServletRequest request, ServletResponse response)
                            throws IOException, ServletException
                    {
                        FilteringWicketTester.this.doFilter(filterIterator, processRequestCycle);
                    }
                });
            }
            else
            {
                processRequestCycle.run();
            }
        }

        @Override
        public void destroy()
        {
            for (Filter filter : servletFilters)
            {
                filter.destroy();
            }

            // This insures that this instance cannot be reused.
            servletFilters = null;

            super.destroy();
        }

        private void initFilters()
        {
            try
            {
                for (int i = 0; i < servletFilters.size(); i++)
                {
                    final int ii = i + 1;
                    Filter filter = servletFilters.get(i);
                    filter.init(new FilterConfig()
                    {
                        public String getFilterName()
                        {
                            return "filter-" + ii;
                        }

                        public ServletContext getServletContext()
                        {
                            return getServletSession().getServletContext();
                        }

                        public String getInitParameter(String name)
                        {
                            return null;
                        }

                        public Enumeration getInitParameterNames()
                        {
                            return null;
                        }
                    });
                }
            }
            catch (ServletException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @BeforeClass
    public void setUpFilteringWicketTesterOnce()
    {
        tester = new FilteringWicketTester(createWebApplication(), createServletFilters());
    }

    @AfterClass
    public void tearDownFilteringWicketTesterOnce()
    {
        tester.destroy();
    }

    @BeforeMethod
    protected void setUpAppEngine() throws Exception
    {
        ApiProxy.setEnvironmentForCurrentThread(new AppEngineEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(new File("."))
        {
        });
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();

        // JDO
        proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.TRUE.toString());
    }

    @AfterMethod
    protected void tearDownAppEngine() throws Exception
    {
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();

        // JDO
        LocalDatastoreService datastoreService = (LocalDatastoreService) proxy.getService(LocalDatastoreService.PACKAGE);
        datastoreService.clearProfiles();

        // not strictly necessary to null these out but there's no harm either
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
    }

    /**
     * The subclass should override this method and return new, uninitialized Filter objects, in the order
     * in which they should be executed.
     *
     * @return the servlet filters; the default implementation returns an empty array
     */
    protected Filter[] createServletFilters()
    {
        return new Filter[0];
    }

    /**
     * The subclass must return a new WebApplication subclass representing the Wicket application being tested.
     *
     * @return the WebApplication instance
     */
    protected abstract WebApplication createWebApplication();

    private static class AppEngineEnvironment implements ApiProxy.Environment
    {
        public String getAppId()
        {
            return "test";
        }

        public String getVersionId()
        {
            return "1.0";
        }

        public String getEmail()
        {
            throw new UnsupportedOperationException();
        }

        public boolean isLoggedIn()
        {
            throw new UnsupportedOperationException();
        }

        public boolean isAdmin()
        {
            throw new UnsupportedOperationException();
        }

        public String getAuthDomain()
        {
            throw new UnsupportedOperationException();
        }

        public String getRequestNamespace()
        {
            return "";
        }

        public Map<String, Object> getAttributes()
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("com.google.appengine.server_url_key", "http://localhost:8080");
            return map;
        }
    }
}