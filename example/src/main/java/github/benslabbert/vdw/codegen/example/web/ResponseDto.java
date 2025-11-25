/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import github.benslabbert.vertxjsonwriter.annotation.JsonWriter;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.Validator;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@JsonWriter
@GenerateBuilder
public record ResponseDto(@Nullable String data) {

  public static ResponseDtoBuilder.Builder builder() {
    return ResponseDtoBuilder.builder();
  }

  @Nullable public static ResponseDto fromJson(JsonObject json) {
    return ResponseDto_JsonWriter.fromJson(json);
  }

  @Nonnull
  public JsonObject toJson() {
    return ResponseDto_JsonWriter.toJson(this);
  }

  @Nonnull
  public static Validator getValidator() {
    return ResponseDto_JsonWriter.getValidator();
  }

  @Nonnull
  static ObjectSchemaBuilder schemaBuilder() {
    return ResponseDto_JsonWriter.schemaBuilder();
  }
}
