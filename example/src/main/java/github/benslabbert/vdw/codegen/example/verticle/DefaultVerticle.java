/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.verticle;

import github.benslabbert.txmanager.PlatformTransactionManager;
import github.benslabbert.vdw.codegen.example.di.DaggerProvider;
import github.benslabbert.vdw.codegen.example.di.Provider;
import github.benslabbert.vdw.codegen.example.web.RouterFactory;
import github.benslabbert.vdw.codegen.example.web.ServerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(DefaultVerticle.class);

  private Provider provider = null;
  private HttpServer httpServer = null;

  private void setHttpServer(HttpServer httpServer) {
    this.httpServer = httpServer;
  }

  public int getPort() {
    return httpServer.actualPort();
  }

  @Override
  public void start(Promise<Void> startPromise) {
    log.info("Starting verticle");
    provider = DaggerProvider.builder().vertx(vertx).config(config()).build();
    provider.init();

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

  @Override
  public void stop(Promise<Void> stopPromise) {
    log.info("Stopping verticle");

    try {
      PlatformTransactionManager.close();
    } catch (Exception e) {
      log.error("failed closing transaction manager", e);
    }

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
