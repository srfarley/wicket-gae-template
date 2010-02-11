package com.example.web;

import com.example.BaseWicketGoogleAppEngineTest;
import com.example.jdo.PersistenceManagerFilter;
import com.google.inject.servlet.GuiceFilter;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebApplication;
import org.testng.annotations.Test;

import javax.servlet.Filter;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage extends BaseWicketGoogleAppEngineTest
{
    @Override
    protected WebApplication createWebApplication()
    {
        return new SampleApplication();
    }

    @Override
    protected Filter[] createServletFilters()
    {
        // Replicate the order of filters as defined in web.xml.
        return new Filter[] { new GuiceFilter(), new PersistenceManagerFilter() };
    }

    @Test
    public void testRenderHomePage()
    {
        final String message1 = "Testing 1..2..3";
        final String message2 = "Foobar";

        // Request the home page.
        tester.startPage(HomePage.class, new PageParameters("message=" + message1));

        // Assert that the home page was rendered.
        tester.assertRenderedPage(HomePage.class);

        // Assert that the message appears where expected.
        tester.assertLabel("messages:0:message", message1);

        // Do it one more time.
        tester.startPage(HomePage.class, new PageParameters("message=" + message2));
        tester.assertRenderedPage(HomePage.class);
        tester.assertLabel("messages:0:message", message1);
        tester.assertLabel("messages:1:message", message2);
    }

    @Test
    public void testRenderGuestbookPage()
    {
        final String message1 = "Testing 1..2..3";
        final String message2 = "Foobar";

        setUserEmail("test@example.com");
        
        // Request the home page.
        tester.startPage(Guestbook.class);

        // Assert that the home page was rendered.
        tester.assertRenderedPage(Guestbook.class);

//        // Assert that the message appears where expected.
//        tester.assertLabel("messages:0:message", message1);
//
//        // Do it one more time.
//        tester.startPage(HomePage.class, new PageParameters("message=" + message2));
//        tester.assertRenderedPage(HomePage.class);
//        tester.assertLabel("messages:0:message", message1);
//        tester.assertLabel("messages:1:message", message2);
    }
}
