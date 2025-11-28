/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.verticle;

import github.benslabbert.vdw.codegen.commons.eb.EventBusServiceConfigurer;
import github.benslabbert.vdw.codegen.example.di.DaggerProvider;
import github.benslabbert.vdw.codegen.example.di.Provider;
import github.benslabbert.vdw.codegen.example.web.RouterFactory;
import github.benslabbert.vdw.codegen.example.web.ServerFactory;
import github.benslabbert.vdw.codegen.txmanager.PlatformTransactionManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.serviceproxy.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(DefaultVerticle.class);
  private static final String ADDR = "ADDR";

  private Provider provider = null;
  private HttpServer httpServer = null;
  long periodic = -1L;

  private void setHttpServer(HttpServer httpServer) {
    this.httpServer = httpServer;
  }

  public int getPort() {
    return httpServer.actualPort();
  }

  @Override
  public void start(Promise<Void> startPromise) {
    log.info("Starting verticle");
    vertx.exceptionHandler(throwable -> log.error("unhandled exception", throwable));

    provider = DaggerProvider.builder().vertx(vertx).config(config()).build();
    provider.init();
    provider.eventBusServiceConfigurers().forEach(EventBusServiceConfigurer::configure);

    eb();

    ServerFactory serverFactory = provider.serverFactory();
    RouterFactory routerFactory = provider.routerFactory();
    Router router = routerFactory.createRouter();

    serverFactory
        .create(router)
        .listen()
        .onComplete(
            res -> {
              if (res.succeeded()) {
                log.info("listening for requests on port: {}", res.result().actualPort());
                setHttpServer(res.result());
                startPromise.complete();
              } else {
                startPromise.fail(res.cause());
              }
            });
  }

  private void eb() {
    // internally, vert.x will always call the handlers in the order they are registered on the
    // eventbus.
    log.info("Starting EB");
    vertx.eventBus().consumer(ADDR, msg -> log.info("c1 received: {}", msg.body()));
    vertx.eventBus().consumer(ADDR, msg -> log.info("c2 received: {}", msg.body()));

    periodic =
        vertx.setPeriodic(
            1000L,
            1000L,
            ignore -> {
              log.info("timer");
              vertx
                  .eventBus()
                  .publish(ADDR, new JsonObject().put("key", "val1"))
                  .publish(ADDR, new JsonObject().put("key", "val2"))
                  .publish(ADDR, new JsonObject().put("key", "val3"));
            });
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    log.info("Stopping verticle");
    vertx.cancelTimer(periodic);

    try {
      PlatformTransactionManager.close();
    } catch (Exception e) {
      log.error("failed closing transaction manager", e);
    }

    provider.proxyHandlers().forEach(ProxyHandler::close);

    if (null != httpServer) {
      log.info("Stopping http server");
      httpServer
          .close()
          .onComplete(
              e -> {
                if (e.succeeded()) {
                  log.info("http server closed");
                  stopPromise.complete();
                } else {
                  log.error("stopping http server failed", e.cause());
                  stopPromise.fail(e.cause());
                }
              });
      return;
    }

    stopPromise.complete();
  }
}
