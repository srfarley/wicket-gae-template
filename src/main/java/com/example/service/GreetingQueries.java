package com.example.service;

import com.example.jdo.JdoQueries;
import com.example.model.Greeting;
import com.google.inject.Singleton;

import javax.jdo.Query;
import java.util.List;

/**
 * This class provides type-safe methods for all queries of Greeting entities.
 */
@Singleton
public class GreetingQueries extends JdoQueries<Greeting>
{
    public GreetingQueries()
    {
        super(Greeting.class);
    }

    public List<Greeting> latest(int max)
    {
        Query query = newQuery();
        query.setOrdering("date desc");
        query.setRange(0, max);
        return toList(query.execute());
    }
}
