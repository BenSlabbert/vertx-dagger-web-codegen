/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.web.Router;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ServerFactory {

  private final Vertx vertx;

  @Inject
  ServerFactory(Vertx vertx) {
    this.vertx = vertx;
  }

  public HttpServer create(Router router) {
    return vertx
        .createHttpServer(
            new HttpServerOptions()
                .setTracingPolicy(TracingPolicy.ALWAYS)
                .setPort(8080)
                .setHost("0.0.0.0"))
        .requestHandler(router);
  }
}
