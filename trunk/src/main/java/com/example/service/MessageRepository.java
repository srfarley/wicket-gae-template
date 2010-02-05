package com.example.service;

import com.example.Messages;
import com.example.model.Message;
import com.google.inject.Inject;

import javax.jdo.PersistenceManager;

public class MessageRepository extends BaseRepository<Message> implements Messages
{
    @Inject
    public MessageRepository(PersistenceManager pm)
    {
        super(Message.class, pm);
    }
}
