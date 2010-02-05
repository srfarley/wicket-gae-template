package com.example;

import com.example.jdo.PersistenceManagerFilter;
import com.google.inject.servlet.GuiceFilter;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.Filter;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage extends BaseTestCase
{
    @Override
    protected WebApplication createWebApplication()
    {
        return new SampleApplication();
    }

    @Override
    protected Filter[] createServletFilters()
    {
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
}
