package github.benslabbert.vdw.codegen.commons;

import static github.benslabbert.vdw.codegen.commons.ContextDataKey.REQUEST_DATA;
import static github.benslabbert.vdw.codegen.commons.ContextDataKey.REQUEST_JSON;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.function.Function;

public final class ValidateRequestBodyHandler {

  private ValidateRequestBodyHandler() {}

  /**
   * Validates the request.<br>
   * If validation fails, the request is failed.
   */
  public static <T> void validateRequestBody(
      RoutingContext ctx, Validator validator, Function<JsonObject, T> jsonMapper) {
    JsonObject json = ctx.get(REQUEST_JSON);
    T d = jsonMapper.apply(json);
    Set<ConstraintViolation<T>> violations = validator.validate(d);
    if (!violations.isEmpty()) {
      ResponseWriterUtil.sendRequestHasViolations(ctx, violations);
      return;
    }
    ctx.put(REQUEST_DATA, d);
    ctx.next();
  }
}
