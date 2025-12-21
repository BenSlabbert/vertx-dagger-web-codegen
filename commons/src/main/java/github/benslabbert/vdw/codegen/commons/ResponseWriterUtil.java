/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResponseWriterUtil {

  private static final Logger log = LoggerFactory.getLogger(ResponseWriterUtil.class);

  private ResponseWriterUtil() {}

  public static void sendEmpty(RoutingContext ctx, int statusCode) {
    ctx.response().setStatusCode(statusCode).end();
  }

  public static void sendBuffer(RoutingContext ctx, int statusCode, Buffer buffer) {
    ctx.response().setStatusCode(statusCode).end(buffer.appendString("\n"));
  }

  public static void sendFuture(RoutingContext ctx, int statusCode, Future<JsonObject> future) {
    future.onFailure(ctx::fail).onSuccess(json -> sendJson(ctx, statusCode, json));
  }

  public static void sendString(RoutingContext ctx, int statusCode, String msg) {
    ctx.response()
        .setStatusCode(statusCode)
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
        .putHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(msg.length()))
        .end(msg + "\n");
  }

  public static void sendJson(RoutingContext ctx, int statusCode, JsonObject json) {
    ctx.response()
        .setStatusCode(statusCode)
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(json.toBuffer().appendString("\n"));
  }

  public static <T> void sendRequestHasViolations(
      RoutingContext ctx, Set<ConstraintViolation<T>> violations) {
    log.error("validations fail for request with {} violations", violations.size());
    violations.forEach(v -> log.error("Property {} : {}", v.getPropertyPath(), v.getMessage()));
    JsonObject json = convert(violations);
    ctx.response()
        .setStatusCode(BAD_REQUEST.code())
        .end(json.toBuffer().appendString("\n"))
        .onFailure(ctx::fail);
  }

  private static <T> JsonObject convert(Set<ConstraintViolation<T>> violations) {
    return new JsonObject()
        .put(
            "errors",
            violations.stream()
                .map(
                    violation ->
                        new JsonObject()
                            .put("field", violation.getPropertyPath().toString())
                            .put("message", violation.getMessage()))
                .toList());
  }
}
