package com.example.service;

import com.example.jdo.JdoRepository;
import com.example.model.Greeting;
import com.google.inject.Singleton;

@Singleton
public class JdoGreetingRepository extends JdoRepository<Greeting>
{
    public JdoGreetingRepository()
    {
        super(Greeting.class);
    }
}