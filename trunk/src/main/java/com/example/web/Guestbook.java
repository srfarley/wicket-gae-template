package com.example.web;

import com.example.Greetings;
import com.example.model.Greeting;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import java.util.Date;
import java.util.List;

public class Guestbook extends WebPage
{
    @Inject
    private Greetings greetings;

    public Guestbook()
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        WebMarkupContainer helloUser = new WebMarkupContainer("hello-user");
        add(helloUser);

        WebMarkupContainer helloAnon = new WebMarkupContainer("hello-anon");
        add(helloAnon);

        Label userNickname = new Label("user-email");
        helloUser.add(userNickname);

        ExternalLink signOut = new ExternalLink("sign-out", userService.createLogoutURL("/" + getRequest().getURL()));
        helloUser.add(signOut);

        ExternalLink signIn = new ExternalLink("sign-in", userService.createLoginURL("/" + getRequest().getURL()));
        helloAnon.add(signIn);

        Label noMessages = new Label("no-messages", "The guestbook has no messages.");
        add(noMessages);

        if (user != null)
        {
            userNickname.setDefaultModel(new Model<String>(user.getEmail()));
            helloAnon.setVisible(false);
        }
        else
        {
            helloUser.setVisible(false);
        }

        // This LoadableDetachableModel allows the following ListView<Greeting> to always load the latest persisted
        // Greeting entities on-demand, without having to store any model data in the session when this Guestbook page
        // gets serialized.
        LoadableDetachableModel<List<Greeting>> latestGreetings = new LoadableDetachableModel<List<Greeting>>()
        {
            @Override
            protected List<Greeting> load()
            {
                return greetings.listLatestGreetings();
            }
        };

        if (!latestGreetings.getObject().isEmpty())
        {
            noMessages.setVisible(false);
        }

        ListView<Greeting> messages = new ListView<Greeting>("messages", latestGreetings)
        {
            @Override
            protected void populateItem(ListItem<Greeting> item)
            {
                Greeting greeting = item.getModel().getObject();
                String email = greeting.getAuthor() != null ? greeting.getAuthor().getEmail() : "An anonymous person ";
                item.add(new Label("message", email + " wrote")); // TODO: bold style around user email
                item.add(new Label("message-content", greeting.getContent()));
            }
        };
        add(messages);

        Form<String> signForm = new Form<String>("sign-form", new Model<String>())
        {
            private TextArea<String> contentField;

            {
                contentField = new TextArea<String>("sign-content", new Model<String>(""));
                add(contentField);
            }

            @Override
            protected void onSubmit()
            {
                String content = contentField.getModelObject();
                Date date = new Date();
                Greeting greeting = new Greeting(user, content, date);
                Guestbook.this.greetings.persist(greeting);
                contentField.setModelObject("");
            }
        };
        add(signForm);
    }
}