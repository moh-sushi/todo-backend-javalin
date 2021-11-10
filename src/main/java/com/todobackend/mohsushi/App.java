package com.todobackend.mohsushi;

import io.javalin.Javalin;
import io.javalin.core.event.EventHandler;
import io.javalin.core.event.EventListener;
import io.javalin.core.util.Header;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.NotFoundResponse;

import java.util.function.Consumer;

public class App {

  public static void main(String[] args) {
    Javalin app = Javalin.create(config -> {
      config.enableDevLogging();
//      config.enableCorsForOrigin("https://www.todobackend.com/");
//      config.contextPath = "/todos";
      config.enableCorsForAllOrigins();
      config.registerPlugin(new RouteOverviewPlugin("/overview"));
    });

    final TodoBackendRepository repository = new TodoBackendRepositoryImpl();
//    repository.create(new TodoBackendEntry(-1L, "http://localhost:7000/-1", "bla", 23L, Boolean.FALSE), "http://localhost:7000/todos/-1");

    mapEndpoints(app, repository);

    app.events(event -> {
      event.serverStarting(HibernateUtil::createSessionFactory);
      event.serverStopping(() -> {
        HibernateUtil.closeSessionFactory();
        System.out.println("is stopping");
      });
      event.serverStopped(() -> System.out.println("stopped"));
    });

    app.start(7000);

    Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
  }

  private static void mapEndpoints(Javalin app, TodoBackendRepository repository) {
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

    app.get("/", ctx -> ctx.json(repository.all()));
    app.delete("/", ctx -> repository.deleteAll());

    app.post("/", ctx -> {
      final TodoBackendEntry entry = ctx.bodyAsClass(TodoBackendEntry.class);
      ctx.json(repository.create(entry, ctx.url()));
    });

    app.get("/<id>", ctx -> {
      TodoBackendEntry entry = repository.get(Long.parseLong(ctx.pathParam("id")));
      if (entry == null) {
        throw new NotFoundResponse("no entry with id " + ctx.pathParam("id"));
      } else {
        ctx.json(entry);
      }
    });

    app.delete("/<id>", ctx -> {
      TodoBackendEntry entry = repository.delete(Long.parseLong(ctx.pathParam("id")));
      if (entry == null) {
        throw new NotFoundResponse("no entry with id " + ctx.pathParam("id"));
      } else {
        ctx.json(entry);
      }
    });

    app.patch("/<id>", ctx -> {
      TodoBackendEntry entry = repository.update(Long.parseLong(ctx.pathParam("id")), ctx.bodyAsClass(TodoBackendEntry.class));
      if (entry == null) {
        throw new NotFoundResponse("no entry with id " + ctx.pathParam("id"));
      } else {
        ctx.json(entry);
      }
    });
  }

}
