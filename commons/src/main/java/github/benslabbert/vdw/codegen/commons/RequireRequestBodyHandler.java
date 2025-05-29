/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.json.schema.OutputUnit;
import io.vertx.json.schema.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequireRequestBodyHandler {

  private static final Logger log = LoggerFactory.getLogger(RequireRequestBodyHandler.class);

  private RequireRequestBodyHandler() {}

  /**
   * Adds the request JSON body to the request context.<br>
   * If the request body is empty, the request is failed.
   */
  public static void requireJsonBody(RoutingContext ctx, Validator jsonValidator, String path) {
    JsonObject json = ctx.body().asJsonObject();
    if (null == json) {
      log.warn("{} request body is empty", path);
      ctx.fail(400);
      return;
    }

    OutputUnit validate = jsonValidator.validate(json);
    if (Boolean.FALSE.equals(validate.getValid())) {
      ctx.response().setStatusCode(400).end();
      return;
    }

    ctx.put(ContextDataKey.REQUEST_JSON, json);
    ctx.next();
  }
}
