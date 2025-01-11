/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import io.vertx.core.json.JsonObject;

public record Dto(String data) {

  public static Dto fromJson(JsonObject json) {
    return new Dto(json.getString("data"));
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("data", data);
    return json;
  }
}
