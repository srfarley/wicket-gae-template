package com.example;

import com.example.model.Message;
import com.google.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Homepage
 */
public class HomePage extends WebPage
{
    @Inject
    private Messages messageRepo;

    /**
     * Constructor that is invoked when page is invoked without a session.
     *
     * @param parameters Page parameters
     */
    public HomePage(final PageParameters parameters)
    {
        String msg = parameters.getString("message");
        msg = msg == null ? "Hello" : msg;
        Message message = new Message();
        message.setText(msg);
        message.setDate(new Date());
        messageRepo.create(message);

        List<Message> messages = new ArrayList<Message>(messageRepo.list());
        add(new ListView<Message>("messages", messages)
        {
            @Override
            protected void populateItem(ListItem<Message> item)
            {
                Message message = item.getModel().getObject();
                item.add(new Label("message", message.getText()));
                item.add(new Label("date", message.getDate().toString()));
            }
        });
    }
}
