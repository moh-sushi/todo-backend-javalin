package com.todobackend.mohsushi;

import com.todobackend.mohsushi.handler.DbTransactionHandler;
import io.javalin.http.Context;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Objects;

public abstract class HibernateUtil {

  private HibernateUtil() {
    // no op
  }

  private static SessionFactory sessionFactory = null;

  public static void set(final SessionFactory sf) {
    sessionFactory = sf;
  }

  public static SessionFactory get() {
    return Objects.requireNonNull(sessionFactory, "SessionFactory not set before? Or SessionFactory has been set to null.");
  }

  public static Session session(final Context context) {
    return (Session) context.req.getAttribute(DbTransactionHandler.class.getSimpleName());
  }
}
