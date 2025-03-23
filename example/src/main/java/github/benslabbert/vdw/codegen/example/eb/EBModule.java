/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.serviceproxy.AuthenticationInterceptor;
import io.vertx.serviceproxy.AuthorizationInterceptor;
import jakarta.inject.Singleton;
import java.util.Set;

@Module(
    includes = {
      DataService_EB_Module_Bindings.class,
      ExampleService_EB_Module_Bindings.class,
      ModuleBindings.class
    })
public interface EBModule {

  @Provides
  @Singleton
  static AuthenticationInterceptor authenticationInterceptor() {
    // this can be a singleton
    return AuthenticationInterceptor.create(
        new AuthenticationProvider() {
          @Override
          public void authenticate(JsonObject jsonObject, Handler<AsyncResult<User>> handler) {
            throw new UnsupportedOperationException("Not implemented");
          }

          @Override
          public Future<User> authenticate(Credentials credentials) {
            return switch (credentials) {
              case TokenCredentials tc:
                String token = tc.getToken();
                yield Future.succeededFuture(User.fromName("name"));
              default:
                throw new IllegalStateException("Unexpected value: " + credentials);
            };
          }
        });
  }

  @Provides
  static AuthorizationInterceptor authorizationInterceptor() {
    return AuthorizationInterceptor.create(
        new AuthorizationProvider() {
          @Override
          public String getId() {
            return "custom-id";
          }

          @Override
          public void getAuthorizations(User user, Handler<AsyncResult<Void>> handler) {
            user.authorizations()
                .add(
                    getId(),
                    Set.of(
                        RoleBasedAuthorization.create("role-1"),
                        RoleBasedAuthorization.create("role-2")));
            handler.handle(Future.succeededFuture());
          }
        });
  }
}
