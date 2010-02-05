package com.example;

import com.example.model.Message;

import java.util.Collection;

public interface Messages
{
    public Collection<Message> list();

    public void create(Message message);

    public void delete(Long id);
}
