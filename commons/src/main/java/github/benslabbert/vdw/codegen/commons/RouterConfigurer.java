/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons;

import io.vertx.ext.web.Router;

public interface RouterConfigurer {
  void route(Router router);
}
