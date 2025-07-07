/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons;

import io.vertx.ext.web.handler.AuthorizationHandler;

public interface RoleAuthorizationHandlerProvider {

  AuthorizationHandler forRole(String role);

  AuthorizationHandler forRoles(String... roles);
}
