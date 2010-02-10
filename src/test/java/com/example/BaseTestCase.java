package com.example;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import org.apache.wicket.protocol.http.WebApplication;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.servlet.*;
import java.io.File;
import java.util.*;

/**
 * This base test class sets up a simulated Google App Engine + Wicket environment.  Wicket pages
 * can be tested using the protected 'tester' field, and development-mode GAE services are made
 * available to tests.
 */
public abstract class BaseTestCase
{
    protected FilteringWicketTester tester;
    private String userEmail;

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

        userEmail = null;
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

    protected final void setUserEmail(String userEmail)
    {
        userEmail = userEmail;
    }

    /**
     * The subclass must return a new WebApplication subclass representing the Wicket application being tested.
     *
     * @return the WebApplication instance
     */
    protected abstract WebApplication createWebApplication();

    private class AppEngineEnvironment implements ApiProxy.Environment
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
            return userEmail;
        }

        public boolean isLoggedIn()
        {
            return getEmail() != null;
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