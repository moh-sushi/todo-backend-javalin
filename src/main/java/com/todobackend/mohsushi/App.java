package com.todobackend.mohsushi;

import com.todobackend.mohsushi.configuration.Configuration;
import com.todobackend.mohsushi.configuration.SystemPropertiesConfigurationImpl;
import com.todobackend.mohsushi.event.H2EventListener;
import com.todobackend.mohsushi.handler.DbExceptionHandler;
import com.todobackend.mohsushi.handler.DbTransactionHandler;
import io.javalin.Javalin;
import io.javalin.core.util.Header;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.core.validation.BodyValidator;
import io.javalin.core.validation.Validator;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;

public class App {

  public static void main(String[] args) {
    final Configuration configuration = new SystemPropertiesConfigurationImpl(System.getProperties());
    Javalin app = Javalin.create(config -> {
      config.enableDevLogging();
//      config.enableCorsForOrigin("https://www.todobackend.com/");
//      config.contextPath = "/todos";
      config.enableCorsForAllOrigins();
      config.registerPlugin(new RouteOverviewPlugin("/overview"));
    });

    mapEndpoints(app);
    app.events(new H2EventListener());
    app.before(DbTransactionHandler.before(HibernateUtil::get));
    app.after(DbTransactionHandler.after(HibernateUtil::get));
    app.exception(Exception.class, new DbExceptionHandler());

    app.start(configuration.jettyConfiguration().port());
    Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
  }

  private static void mapEndpoints(Javalin app) {
    // alle responses -> json
    // FIXME / get -> alle todos anzeigen
    // FIXME / delete -> alle todos loeschen
    // ----
    // FIXME / post json todo -> todo anlegen
    // FIXME /{id} get json todo -> todo mit id laden
    // FIXME /{id} delete json todo -> todo mit id loeschen
    // FIXME /{id} patch json todo -> todo mit id aktualisieren

    app.before(ctx -> {
      if ("OPTIONS".equals(ctx.method())) {
        ctx.header(Header.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      }
      if (ctx.header(Header.ACCESS_CONTROL_REQUEST_HEADERS) != null) {
        ctx.header(Header.ACCESS_CONTROL_ALLOW_HEADERS);
      }
      if (ctx.header(Header.ACCESS_CONTROL_REQUEST_METHOD) != null) {
        ctx.header(Header.ACCESS_CONTROL_ALLOW_METHODS);
      }
    });

    app.get("/", ctx -> ctx.json(new TodoBackendRepositoryHibernateImpl(ctx).all()));
    app.delete("/", ctx -> new TodoBackendRepositoryHibernateImpl(ctx).deleteAll());

    app.post("/", ctx -> {
      BodyValidator<TodoBackendEntry> bodyValidator = ctx.bodyValidator(TodoBackendEntry.class);
      if (!bodyValidator.errors().isEmpty()) throw new InternalServerErrorResponse("no valid input data");
      ctx.json(new TodoBackendRepositoryHibernateImpl(ctx).create(bodyValidator.get(), ctx.url()));
    });

    app.get("/<id>", ctx -> {
      Validator<Long> idValidator = ctx.pathParamAsClass("id", Long.class);
      if(!idValidator.errors().isEmpty()) {
        ctx.status(404);
        return;
      }
      TodoBackendEntry entry = new TodoBackendRepositoryHibernateImpl(ctx).get(idValidator.get());
      if (entry == null) {
        throw new NotFoundResponse("no entry with id " + ctx.pathParam("id"));
      } else {
        ctx.json(entry);
      }
    });

    app.delete("/<id>", ctx -> {
      TodoBackendEntry entry = new TodoBackendRepositoryHibernateImpl(ctx).delete(Long.parseLong(ctx.pathParam("id")));
      if (entry == null) {
        throw new NotFoundResponse("no entry with id " + ctx.pathParam("id"));
      } else {
        ctx.json(entry);
      }
    });

    app.patch("/<id>", ctx -> {
      TodoBackendEntry entry = new TodoBackendRepositoryHibernateImpl(ctx).update(Long.parseLong(ctx.pathParam("id")), ctx.bodyAsClass(TodoBackendEntry.class));
      if (entry == null) {
        throw new NotFoundResponse("no entry with id " + ctx.pathParam("id"));
      } else {
        ctx.json(entry);
      }
    });
  }

}
