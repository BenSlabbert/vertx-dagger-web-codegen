/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import io.vertx.core.json.JsonObject;
import jakarta.annotation.Nonnull;

public record ResponseDto(String data) {

  @Nonnull
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("data", data);
    return json;
  }
}
