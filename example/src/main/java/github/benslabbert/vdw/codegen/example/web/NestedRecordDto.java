/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

/**
 * Example of nested records with @GenerateBuilder and @JsonWriter annotations. This demonstrates
 * that both annotations work correctly together for nested record types.
 */
@JsonWriter
@GenerateBuilder
public record NestedRecordDto(@NotBlank String name, @Nullable InnerDto inner) {

  public static NestedRecordDtoBuilder.Builder builder() {
    return NestedRecordDtoBuilder.builder();
  }

  /** Inner record that also uses @GenerateBuilder and @JsonWriter annotations. */
  @JsonWriter
  @GenerateBuilder
  public record InnerDto(@NotBlank String innerName, @Nullable Integer count) {

    public static NestedRecordDto_InnerDtoBuilder.Builder builder() {
      return NestedRecordDto_InnerDtoBuilder.builder();
    }
  }
}
