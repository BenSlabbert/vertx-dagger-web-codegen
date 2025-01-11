/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.config;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import github.benslabbert.vdw.codegen.commons.RoleAuthorizationHandlerProvider;
import github.benslabbert.vdw.codegen.commons.ValidatorProvider;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;

@Module
interface ConfigModuleBindings {

  @Binds
  @IntoSet
  AutoCloseable closeable(ValidatorFactoryProvider p);

  @Binds
  ValidatorProvider validatorProvider(ValidatorFactoryProvider p);

  @Binds
  RoleAuthorizationHandlerProvider roleAuthorizationHandlerProvider(AuthorizationHandlerProvider p);

  @Binds
  AuthorizationProvider authorizationProvider(AuthorizationHandlerProvider p);

  @Binds
  AuthenticationProvider authenticationProvider(AuthenticationHandlerProvider p);
}
