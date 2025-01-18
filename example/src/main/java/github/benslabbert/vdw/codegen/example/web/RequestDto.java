/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import io.vertx.core.json.JsonObject;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record RequestDto(@NotBlank String data) {

  @Nonnull
  public static RequestDto fromJson(JsonObject jsonObject) {
    if (null == jsonObject) {
      jsonObject = new JsonObject();
    }
    Objects.requireNonNull(jsonObject, "jsonObject cannot be null.");
    String data = jsonObject.getString("data", "");
    return new RequestDto(data);
  }
}
