/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.eb;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authorization.OrAuthorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.serviceproxy.AuthorizationInterceptor;
import io.vertx.serviceproxy.HelperUtils;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.impl.InterceptorHolder;
import jakarta.inject.Provider;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProxyHandlerUtils {

  private static final Logger log = LoggerFactory.getLogger(ProxyHandlerUtils.class);

  private ProxyHandlerUtils() {}

  public static InterceptorHolder roleForAction(
      Provider<AuthorizationInterceptor> authorizationInterceptorProvider,
      String action,
      String role) {
    RoleBasedAuthorization authorization = RoleBasedAuthorization.create(role);
    AuthorizationInterceptor authorizationInterceptor = authorizationInterceptorProvider.get();
    return new InterceptorHolder(action, authorizationInterceptor.addAuthorization(authorization));
  }

  public static InterceptorHolder rolesForAction(
      Provider<AuthorizationInterceptor> authorizationInterceptorProvider,
      String action,
      String... roles) {
    OrAuthorization orAuthorization = OrAuthorization.create();
    for (String role : roles) {
      orAuthorization.addAuthorization(RoleBasedAuthorization.create(role));
    }
    AuthorizationInterceptor authorizationInterceptor = authorizationInterceptorProvider.get();
    return new InterceptorHolder(
        action, authorizationInterceptor.addAuthorization(orAuthorization));
  }

  public static void replyWithError(Message<JsonObject> msg, Throwable t) {
    if (log.isDebugEnabled()) {
      msg.reply(new ServiceException(500, t.getMessage(), HelperUtils.generateDebugInfo(t)));
    } else {
      msg.reply(new ServiceException(500, t.getMessage()));
    }
  }

  public static void handleMessage(
      Message<JsonObject> msg, BiFunction<String, JsonObject, Future<JsonObject>> function) {
    try {
      JsonObject json = msg.body();
      String action = msg.headers().get("action");
      JsonObject request = json.getJsonObject("request");

      if (null == action) {
        msg.reply(new ServiceException(400, "action is null"));
        return;
      }

      if (null == request) {
        msg.reply(new ServiceException(400, "request is null"));
        return;
      }

      if (log.isDebugEnabled()) {
        log.atDebug()
            .setMessage("action: {} with payload: {}")
            .addArgument(action)
            .addArgument(json)
            .log();
      }

      function
          .apply(action, request)
          .onComplete(
              res -> {
                if (res.failed()) {
                  HelperUtils.manageFailure(msg, res.cause(), log.isDebugEnabled());
                } else {
                  msg.reply(null == res.result() ? null : res.result());
                }
              });
    } catch (Throwable t) {
      replyWithError(msg, t);
      throw t;
    }
  }
}
