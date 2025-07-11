/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
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
    return AuthenticationInterceptor.create(
        credentials ->
            switch (credentials) {
              case TokenCredentials tc -> Future.succeededFuture(User.fromName("name"));
              case null -> throw new IllegalStateException("cannot be null");
              default -> throw new IllegalStateException("Unexpected value: " + credentials);
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
          public Future<Void> getAuthorizations(User user) {
            user.authorizations()
                .put(
                    getId(),
                    Set.of(
                        RoleBasedAuthorization.create("role-1"),
                        RoleBasedAuthorization.create("role-2")));
            return Future.succeededFuture();
          }
        });
  }
}
