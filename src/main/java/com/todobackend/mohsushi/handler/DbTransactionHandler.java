package com.todobackend.mohsushi.handler;

import com.todobackend.mohsushi.HibernateUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class DbTransactionHandler implements Handler {

  private final HandlerType handlerType;
  private final Supplier<SessionFactory> ssf;

  DbTransactionHandler(HandlerType handlerType, Supplier<SessionFactory> ssf) {
    this.handlerType = Objects.requireNonNull(handlerType);
    this.ssf = Objects.requireNonNull(ssf);
  }

  @Override
  public void handle(@NotNull Context ctx) throws Exception {
    switch (handlerType) {
      case BEFORE -> {
        final Session session = ssf.get().openSession();
        session.beginTransaction();
        HibernateUtil.setSession(ctx, session);
      }
      case AFTER -> {
        try (Session session = Objects.requireNonNull(HibernateUtil.session(ctx))) {
          boolean open = session.isOpen();
          boolean active = session.getTransaction().isActive();
          if (open && active) {
            session.getTransaction().commit();
          }
        }
      }
      default -> throw new RuntimeException(handlerType.name());
    }
  }

  public static DbTransactionHandler before(final Supplier<SessionFactory> ssf) {
    return new DbTransactionHandler(HandlerType.BEFORE, ssf);
  }

  public static DbTransactionHandler after(final Supplier<SessionFactory> ssf) {
    return new DbTransactionHandler(HandlerType.AFTER, ssf);
  }

  private static enum HandlerType {
    BEFORE, AFTER
  }

}
