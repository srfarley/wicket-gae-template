This project provides a template to help you get started with building a [Wicket](http://wicket.apache.org/) application for [Google App Engine](http://code.google.com/appengine/).  It is a re-implementation of the Guestbook application described by the App Engine [tutorial](http://code.google.com/appengine/docs/java/gettingstarted/).  Whereas the tutorial uses raw servlets and JSP to demonstrate a some of the basic features of App Engine, this project uses Wicket as the web framework.

In addition, the project defines classes for handing persistence using [JDO](http://code.google.com/appengine/docs/java/datastore/), and uses [Google Guice](http://code.google.com/p/google-guice/) to inject instances of these classes into the Wicket pages for interacting with the App Engine datastore.

For unit testing, the project contains base classes that set up the App Engine development environment so you can write tests against the full stack, including those that interact directly with the Wicket pages.  The testing framework is [TestNG](http://testng.org/doc/index.html), but it is possible to convert them to JUnit tests with some work.

The current release of this project is dependent on the following library versions:

  * Google App Engine SDK 1.3.1
  * Wicket 1.4.6
  * Guice 2.0
  * TestNG 5.8

You need at least Maven 2.2.0 to build the project.  Executing **`mvn package`** builds the web application under `target/wicket-gae-template-1.1-SNAPSHOT`.

The [maven-gae-plugin](http://code.google.com/p/maven-gae-plugin/) is defined in the Maven POM, and can optionally be used to download and interact with the Google App Engine SDK tools.  To download the SDK and install it into your local Maven repository, execute **`mvn gae:unpack`** one time.  From then on, you can build and run your application using **`mvn gae:run`**.  If you want to deploy to appspot.com, set the `<application>` name in `src/main/webapp/WEB-INF/appengine-web.xml` to your reserved application name, and then execute **`mvn gae:deploy`**.

A live demo is running at http://wicket-gae-template.appspot.com/.