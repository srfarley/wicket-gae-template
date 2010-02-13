package com.example.service;

import com.example.Greetings;
import com.example.model.Greeting;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.jdo.PersistenceManager;
import java.util.List;

@Singleton
public class GreetingRepository extends JdoRepository<Greeting> implements Greetings
{
    @Inject
    public GreetingRepository(Provider<PersistenceManager> pmProvider)
    {
        super(Greeting.class, pmProvider);
    }

    public List<Greeting> listLatestGreetings()
    {
        return listUsingNamedQuery("latestGreetings");
    }
}