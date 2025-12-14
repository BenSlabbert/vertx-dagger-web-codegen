/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.test;

import github.benslabbert.vdw.codegen.config.ApplicationConfig;
import io.vertx.core.json.JsonObject;

public final class ConfigEncoder {

  private ConfigEncoder() {}

  public static JsonObject encode(ApplicationConfig config) {
    String key = "__emptyPlaceholder";
    String encode = new JsonObject().put(key, config).encode();
    return new JsonObject(encode).getJsonObject(key);
  }
}
