/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.eb;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.internal.ContextInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.serviceproxy.ServiceInterceptor;
import io.vertx.serviceproxy.impl.InterceptorHolder;
import java.util.Map;

public final class AddUserToContextServiceInterceptor implements ServiceInterceptor {

  private AddUserToContextServiceInterceptor() {}

  public static InterceptorHolder create() {
    return new InterceptorHolder(new AddUserToContextServiceInterceptor());
  }

  @Override
  public Future<Message<JsonObject>> intercept(
      Vertx vertx, Map<String, Object> interceptorContext, Message<JsonObject> body) {
    User user = (User) interceptorContext.get("user");
    vertx.getOrCreateContext().put("user", user);
    final ContextInternal vertxContext = (ContextInternal) vertx.getOrCreateContext();
    return vertxContext.succeededFuture(body);
  }
}
