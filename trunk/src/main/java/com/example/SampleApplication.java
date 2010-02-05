package com.example;

import com.example.jdo.PersistenceManagerFilter;
import com.example.service.MessageRepository;
import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;

/**
 * Application object for your web application.
 */
public class SampleApplication extends WebApplication
{
    /**
     * Constructor
     */
    public SampleApplication()
    {
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    public Class<HomePage> getHomePage()
    {
        return HomePage.class;
    }

    @Override
    protected void init()
    {
        super.init();

        // for Google App Engine
        getResourceSettings().setResourcePollFrequency(null);

        // Yay, Guice!
        addComponentInstantiationListener(new GuiceComponentInjector(this, getServletModule()/*,
                                                                     new ApplicationModule()*/));
    }

    @Override
    protected ISessionStore newSessionStore()
    {
        // for Google App Engine
        return new HttpSessionStore(this);
    }

    private ServletModule getServletModule()
    {
        // Use of a ServletModule provides @RequestScoped and @SessionScoped injection.
        // http://code.google.com/p/google-guice/wiki/ServletModule
        return new ServletModule()
        {
            @Override
            protected void configureServlets()
            {
                // This doesn't work; Guice complains about not being a singleton even if @Singleton is applied.
//                filter("/*").through(PersistenceManagerFilter.class);

                // Wicket goes right to the web.xml file, so these don't work.
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("applicationClassName", com.example.SampleApplication.class.getName());
//                filter("/*").through(org.apache.wicket.protocol.http.WicketFilter.class, params);

                // Enable per-request thread PersistenceManager injection.
                PersistenceManagerFilter.bindPersistenceManager(binder());

                // business object bindings go here
                bind(Messages.class).to(MessageRepository.class);

            }
        };
    }

//    private static class ApplicationModule extends AbstractModule
//    {
//        @Override
//        protected void configure()
//        {
//            // business object bindings go here
//            bind(Messages.class).to(MessageRepository.class);
//        }
//    }
}
