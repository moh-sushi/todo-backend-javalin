package com.todobackend.mohsushi.handler;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DbExceptionHandler implements ExceptionHandler<Exception> {

  @Override
  public void handle(@NotNull Exception exception, @NotNull Context ctx) {
    try (Session session = Objects.requireNonNull((Session) ctx.req.getAttribute(DbTransactionHandler.class.getSimpleName()))) {
      session.getTransaction().rollback();
    }
    ctx.status(500); // if not set, 200 will be given back to client
  }
}
