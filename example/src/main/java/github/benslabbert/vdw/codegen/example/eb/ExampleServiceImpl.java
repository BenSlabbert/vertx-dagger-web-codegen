/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class ExampleServiceImpl implements ExampleService {

  private static final Logger log = LoggerFactory.getLogger(ExampleServiceImpl.class);

  private final Vertx vertx;

  @Inject
  ExampleServiceImpl(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public Future<ExampleResponse> execute(ExampleRequest request) {
    User user = vertx.getOrCreateContext().get("user");

    log.atInfo()
        .setMessage("user: {} Received request: {}")
        .addArgument(user::subject)
        .addArgument(request)
        .log();

    return Future.succeededFuture(new ExampleResponse(request.data()));
  }
}
