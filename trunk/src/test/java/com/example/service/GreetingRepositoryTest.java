package com.example.service;

import com.example.BaseGoogleAppEngineTest;
import com.example.model.Greeting;
import com.google.appengine.api.users.User;
import com.google.inject.Provider;
import org.testng.annotations.*;

import static org.testng.Assert.*;

import javax.jdo.PersistenceManager;
import java.util.*;

/**
 * This is a test of the GreetingRepository in isolation, without invoking Wicket.
 */
public class GreetingRepositoryTest extends BaseGoogleAppEngineTest
{
    private PersistenceManager pm;
    private GreetingRepository greetingRepo;
    private GreetingQueries greetingQueries;

    @BeforeMethod
    public void setUp()
    {
        pm = persistenceManagerFactory.getPersistenceManager();

        final Provider<PersistenceManager> pmProvider = new Provider<PersistenceManager>()
        {
            @Override
            public PersistenceManager get()
            {
                return pm;
            }
        };

        greetingRepo = new GreetingRepository()
        {
            {
                setPersistenceManagerProvider(pmProvider);
            }
        };

        greetingQueries = new GreetingQueries()
        {
            {
                setPersistenceManagerProvider(pmProvider);
            }
        };
    }

    @AfterMethod
    public void tearDown()
    {
        pm.close();
    }

    @Test
    public void testPersistWithAuthenticatedUser()
    {
        final String userEmail = "foo@example.com";
        final String content = "Hello, World!";
        final Date date = new Date();
        User author = new User(userEmail, "");
        Greeting greeting = new Greeting(author, content, date);

        greetingRepo.persist(greeting);

        assertNotNull(greeting.getId());
        assertNotNull(greetingQueries.getById(greeting.getId()));
    }

    @Test
    public void testPersistWithAnonymousUser()
    {
        final String content = "Hello, World!";
        final Date date = new Date();
        Greeting greeting = new Greeting(null, content, date);

        greetingRepo.persist(greeting);

        assertNotNull(greeting.getId());
        assertNotNull(greetingQueries.getById(greeting.getId()));
    }

    @Test
    public void testLatestGreetingsQuery()
    {
        final Calendar calendar = Calendar.getInstance();
        final String content = "%d: Hello, World!";
        final Date baseDate = calendar.getTime();
        final int total = 7;
        final int max = 4;
        List<Date> dates = new ArrayList<Date>();
        for (int i = 0; i < total; i++)
        {
            calendar.setTime(baseDate);
            calendar.add(Calendar.MINUTE, i);
            Date date = calendar.getTime();
            dates.add(date);
            Greeting greeting = new Greeting(null, String.format(content, i), date);
            greetingRepo.persist(greeting);
        }

        // The latest greetings are returned in descending date order.
        List<Greeting> greetings = greetingQueries.latest(max);
        
        assertEquals(greetings.size(), max);
        Collections.reverse(greetings);
        dates = dates.subList(total - max, total);
        for (int i = 0; i < max; i++)
        {
            Greeting greeting = greetings.get(i);
            assertEquals(greeting.getContent(), String.format(content, total - max + i));
            assertEquals(greeting.getDate(), dates.get(i));
        }
    }
}
