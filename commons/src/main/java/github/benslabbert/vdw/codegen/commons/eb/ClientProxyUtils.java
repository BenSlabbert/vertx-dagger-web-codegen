/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.eb;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import java.util.function.Function;

public final class ClientProxyUtils {

  private static final String ACTION_HEADER = "action";
  private static final String REQUEST_KEY = "request";

  private ClientProxyUtils() {}

  public static <T> Future<T> getResponseFuture(
      Vertx vertx,
      String address,
      DeliveryOptions options,
      JsonObject reqJson,
      String action,
      Function<JsonObject, T> function) {

    JsonObject json = new JsonObject();
    json.put(REQUEST_KEY, reqJson);

    DeliveryOptions deliveryOptions =
        options == null ? new DeliveryOptions() : new DeliveryOptions(options);
    deliveryOptions.addHeader(ACTION_HEADER, action);
    deliveryOptions.getHeaders().set(ACTION_HEADER, action);

    return vertx
        .eventBus()
        .<JsonObject>request(address, json, deliveryOptions)
        .map(msg -> msg.body() != null ? function.apply(msg.body()) : null);
  }
}
