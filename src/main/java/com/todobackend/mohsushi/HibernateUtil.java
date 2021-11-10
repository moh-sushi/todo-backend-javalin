package com.todobackend.mohsushi;

import org.h2.tools.Server;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;

public abstract class HibernateUtil {

  private HibernateUtil() {
    // no op
  }

  private static SessionFactory sessionFactory = null;
  private static Server h2Server = null;

  public static void createSessionFactory() {
    Configuration config = new Configuration();
    config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
    config.setProperty("hibernate.connection.url", "jdbc:h2:~/todo-backend-javalin;AUTO_SERVER=true");
    config.setProperty("hibernate.connection.username", "sa");
    config.setProperty("hibernate.connection.password", "sa");
    config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    config.setProperty("hibernate.show_sql", "true");
    config.setProperty("hibernate.hbm2ddl", "create-drop");
    config.addAnnotatedClass(TodoBackendEntry.class);

    sessionFactory = config.buildSessionFactory();

//    try {
//      h2Server = Server.createWebServer("-webPort", "7777").start();
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
  }

  public static void closeSessionFactory() {
    if (sessionFactory != null) sessionFactory.close();
//    if (h2Server != null) h2Server.stop();
  }
}
