/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.Validator;

public record Dto(String data) {

  public static Dto fromJson(JsonObject json) {
    return new Dto(json.getString("data"));
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("data", data);
    return json;
  }

  public static Validator getValidator() {
    return null;
  }

  public static Builder builder() {
    return null;
  }

  public interface Builder {}
}
