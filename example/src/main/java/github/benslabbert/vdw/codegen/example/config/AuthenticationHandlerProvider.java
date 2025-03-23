/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.config;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
class AuthenticationHandlerProvider implements AuthenticationProvider {

  @Inject
  AuthenticationHandlerProvider() {}

  @Override
  public void authenticate(JsonObject jsonObject, Handler<AsyncResult<User>> handler) {
    handler.handle(
        Future.failedFuture(
            new UnsupportedOperationException(
                "authenticate(JsonObject, Handler<AsyncResult<User>>) not supported")));
  }

  @Override
  public Future<User> authenticate(Credentials credentials) {
    if (credentials instanceof UsernamePasswordCredentials c) {
      c.checkValid(null);
      if ("name".equals(c.getUsername()) && "password".equals(c.getPassword())) {
        return Future.succeededFuture(User.fromName(c.getUsername()));
      }
    }
    return Future.failedFuture("Invalid credentials");
  }
}
