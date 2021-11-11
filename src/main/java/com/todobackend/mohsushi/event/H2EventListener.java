package com.todobackend.mohsushi.event;

import com.todobackend.mohsushi.HibernateUtil;
import com.todobackend.mohsushi.TodoBackendEntry;
import io.javalin.core.event.EventListener;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.function.Consumer;

public class H2EventListener implements Consumer<EventListener> {

  private SessionFactory sessionFactory;

  @Override
  public void accept(EventListener eventListener) {
    eventListener.serverStarting(() -> {
      Configuration config = new Configuration();
      config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
      config.setProperty("hibernate.connection.url", "jdbc:h2:~/todo-backend-javalin;AUTO_SERVER=true");
      config.setProperty("hibernate.connection.username", "sa");
      config.setProperty("hibernate.connection.password", "sa");
      config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
      config.setProperty("hibernate.show_sql", "true");
      config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
      config.addAnnotatedClass(TodoBackendEntry.class);
      sessionFactory=config.buildSessionFactory();
      HibernateUtil.set(sessionFactory);
    });

    eventListener.serverStopping(() -> {
      HibernateUtil.set(null);
      sessionFactory.close();
      sessionFactory = null;
    });
  }
}
