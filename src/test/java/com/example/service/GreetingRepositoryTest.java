package com.example.service;

import com.example.BaseGoogleAppEngineTest;
import com.example.model.Greeting;
import com.google.appengine.api.users.User;
import com.google.inject.Provider;
import org.testng.annotations.*;

import static org.testng.Assert.*;

import javax.jdo.PersistenceManager;
import java.util.Date;

/**
 *
 */
public class GreetingRepositoryTest extends BaseGoogleAppEngineTest
{
    private PersistenceManager pm;
    private GreetingRepository greetingRepo;

    @BeforeMethod
    public void setUp()
    {
        pm = persistenceManagerFactory.getPersistenceManager();
        greetingRepo = new GreetingRepository(new Provider<PersistenceManager>()
        {
            @Override
            public PersistenceManager get()
            {
                return pm;
            }
        });
    }

    @AfterMethod
    public void tearDown()
    {
        pm.close();
    }

    @Test
    public void testPersistWithLoggedInUser()
    {
        final String userEmail = "foo@example.com";
        final String content = "Hello, World!";
        final Date date = new Date();
        User author = new User(userEmail, "");
        Greeting greeting = new Greeting(author, content, date);
        greetingRepo.persist(greeting);

        assertNotNull(greeting.getId());
        Greeting greetingP = greetingRepo.getById(greeting.getId());
        assertEquals(greeting.getAuthor(), greetingP.getAuthor());
        assertEquals(greeting.getContent(), greetingP.getContent());
        assertEquals(greeting.getDate(), greetingP.getDate());
    }
}
