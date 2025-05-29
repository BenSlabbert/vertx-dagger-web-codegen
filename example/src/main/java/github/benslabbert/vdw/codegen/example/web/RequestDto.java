/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import com.google.auto.value.AutoBuilder;
import github.benslabbert.vertxjsonwriter.annotation.JsonWriter;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.Validator;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@JsonWriter
public record RequestDto(@NotBlank String data) {

  public static Builder builder() {
    return new AutoBuilder_RequestDto_Builder();
  }

  @Nullable public static RequestDto fromJson(JsonObject json) {
    return RequestDto_JsonWriter.fromJson(json);
  }

  @Nonnull
  public JsonObject toJson() {
    return RequestDto_JsonWriter.toJson(this);
  }

  @Nonnull
  public static Validator getValidator() {
    return RequestDto_JsonWriter.getValidator();
  }

  @Nonnull
  static ObjectSchemaBuilder schemaBuilder() {
    return RequestDto_JsonWriter.schemaBuilder();
  }

  @AutoBuilder
  public interface Builder {
    Builder data(String data);

    RequestDto build();
  }
}
