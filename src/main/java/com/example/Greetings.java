package com.example;

import com.example.model.Greeting;

import java.util.List;

/**
 *
 */
public interface Greetings extends Repository<Greeting>
{
    List<Greeting> listLatestGreetings();
}
