package com.example.service;

import com.example.GreetingQueries;
import com.example.jdo.JdoQueries;
import com.example.model.Greeting;
import com.google.inject.Singleton;

import javax.jdo.Query;
import java.util.List;

/**
 * This class provides an implementation of GreetingQueries using JDO Query objects.
 */
@Singleton
public class JdoGreetingQueries extends JdoQueries<Greeting> implements GreetingQueries
{
    public JdoGreetingQueries()
    {
        super(Greeting.class);
    }

    @Override
    public List<Greeting> latest(int max)
    {
        Query query = newQuery();
        query.setOrdering("date desc");
        query.setRange(0, max);
        return toList(query.execute());
    }
}
