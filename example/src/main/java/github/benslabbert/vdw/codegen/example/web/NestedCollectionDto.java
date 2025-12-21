/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Example of nested records with collections using @GenerateBuilder and @JsonWriter annotations.
 * This demonstrates that both annotations work correctly together for nested record types with
 * collection parameters.
 */
@JsonWriter
@GenerateBuilder
public record NestedCollectionDto(
    @NotBlank String title, @NotNull List<ItemDto> items, @NotNull List<String> tags) {

  /**
   * Inner record that represents an item in the collection. This also uses @GenerateBuilder
   * and @JsonWriter annotations.
   */
  @JsonWriter
  @GenerateBuilder
  public record ItemDto(
      @NotBlank String name, @NotNull Integer quantity, @NotNull List<String> properties) {}
}
