package com.example;

import com.example.model.Greeting;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import javax.jdo.PersistenceManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Guestbook extends WebPage
{
    private Provider<PersistenceManager> pm;

    private ListView<Greeting> messages;

    @Inject
    public void setPersistenceManager(Provider<PersistenceManager> pm)
    {
        this.pm = pm;
    }

    public Guestbook()
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        WebMarkupContainer helloUser = new WebMarkupContainer("hello-user");
        add(helloUser);

        WebMarkupContainer helloAnon = new WebMarkupContainer("hello-anon");
        add(helloAnon);

        Label userNickname = new Label("user-nickname");
        helloUser.add(userNickname);

        ExternalLink signOut = new ExternalLink("sign-out", userService.createLogoutURL("/" + getRequest().getURL()));
        helloUser.add(signOut);

        ExternalLink signIn = new ExternalLink("sign-in", userService.createLoginURL("/" + getRequest().getURL()));
        helloAnon.add(signIn);

        Label noMessages = new Label("no-messages"); // TODO: Make this appear when empty; try a wrapper
        add(noMessages);

        if (user != null)
        {
            userNickname.setDefaultModel(new Model<String>(user.getNickname()));
            helloAnon.setVisible(false);
        }
        else
        {
            helloUser.setVisible(false);
        }

        List<Greeting> greetings = listGreetings();
        if (!greetings.isEmpty())
        {
            noMessages.setVisible(false);
        }

        messages = new ListView<Greeting>("messages", greetings)
        {
            @Override
            protected void populateItem(ListItem<Greeting> item)
            {
                Greeting greeting = item.getModel().getObject();
                String nickname = greeting.getAuthor() != null ? greeting.getAuthor().getNickname()
                        : "An anonymous person ";
                item.add(new Label("message", nickname + " wrote ")); // TODO: bold style around user nickname
                item.add(new Label("message-content", greeting.getContent()));
            }
        };
        add(messages);

        Form<String> signForm = new Form<String>("sign-form", new Model<String>())
        {
            private final String defaultMessage = "put your message here";
            private TextArea<String> contentField;

            {
                contentField = new TextArea<String>("sign-content", new Model<String>(defaultMessage));
                add(contentField);
            }

            @Override
            protected void onSubmit()
            {
                String content = contentField.getModelObject();
                Date date = new Date();
                Greeting greeting = new Greeting(user, content, date);
                pm.get().makePersistent(greeting);
                messages.setList(listGreetings());
            }
        };
        add(signForm);
    }

    private List<Greeting> listGreetings()
    {
        String query = "select from " + Greeting.class.getName() + " order by date desc range 0,5";
        List<Greeting> greetings = (List<Greeting>) pm.get().newQuery(query).execute();
        // This effectively detaches the list from JDO so the Guestbook page can be stored in the session.
        // TODO: prevent the list from being serialized as part of the page.
        return new ArrayList<Greeting>(greetings);
    }
}
