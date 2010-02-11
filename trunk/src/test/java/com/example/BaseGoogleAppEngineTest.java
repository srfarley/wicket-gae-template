package com.example;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This base test class sets up a simulated Google App Engine environment.  Development-mode GAE services are made
 * available to tests.
 *
 * TODO: User test helper classes as documented at http://code.google.com/appengine/docs/java/tools/localunittesting.html
 */
public abstract class BaseGoogleAppEngineTest
{
    private String userEmail;
    private boolean isAdmin;
    protected PersistenceManagerFactory persistenceManagerFactory;

    @BeforeClass
    public void createPersistenceManagerFactory()
    {
        persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory("transactions-optional");
    }

    @AfterClass
    public void closePersistenceManagerFactory()
    {
        persistenceManagerFactory.close();
    }
    
    @BeforeMethod
    public final void setUpAppEngine() throws Exception
    {
        ApiProxy.setEnvironmentForCurrentThread(new AppEngineEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(new File("."))
        {
        });
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();

        // JDO storage is in-memory
        proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.TRUE.toString());

        // prevents the exception that complains about creating more than one PersistenceManagerFactory
        System.setProperty("appengine.orm.disable.duplicate.pmf.exception", Boolean.TRUE.toString());

        userEmail = null;
        isAdmin = false;
    }

    @AfterMethod
    public final void tearDownAppEngine() throws Exception
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
     * Sets the email address of the currently logged-in user.  This is set to null before every test method.
     *
     * @param userEmail the user's email address
     */
    protected final void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    /**
     * Flags the currently logged-in user as an administrator.  This is set to false before every test method.
     *
     * @param isAdmin whether or not the user is an administrator
     */
    protected final void setAdmin(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
    }

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
            return isAdmin;
        }

        public String getAuthDomain()
        {
            return "";
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
