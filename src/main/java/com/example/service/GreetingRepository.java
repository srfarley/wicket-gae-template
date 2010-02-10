package com.example.service;

import com.example.Greetings;
import com.example.model.Greeting;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.jdo.PersistenceManager;
import java.util.List;

public class GreetingRepository extends JdoRepository<Greeting> implements Greetings
{
    @Inject
    public GreetingRepository(Provider<PersistenceManager> pmProvider)
    {
        super(Greeting.class, pmProvider);
    }

    public List<Greeting> listRecentGreetings()
    {
        return list("select from " + Greeting.class.getName() + " order by date desc range 0,5");
    }
}