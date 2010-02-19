package com.example.service;

import com.example.jdo.JdoRepository;
import com.example.model.Greeting;
import com.google.inject.Singleton;

@Singleton
public class GreetingRepository extends JdoRepository<Greeting>
{
    public GreetingRepository()
    {
        super(Greeting.class);
    }
}