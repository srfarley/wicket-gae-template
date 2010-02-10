package com.example.service;

import com.example.model.Message;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.jdo.PersistenceManager;

public class MessageRepository extends JdoRepository<Message>
{
    @Inject
    public MessageRepository(Provider<PersistenceManager> pmProvider)
    {
        super(Message.class, pmProvider);
    }
}
