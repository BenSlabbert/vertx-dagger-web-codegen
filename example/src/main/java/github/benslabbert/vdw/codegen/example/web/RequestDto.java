/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import github.benslabbert.vertxjsonwriter.annotation.JsonWriter;
import jakarta.validation.constraints.NotBlank;

@JsonWriter
@GenerateBuilder
public record RequestDto(@NotBlank String data) {

  public static RequestDtoBuilder.Builder builder() {
    return RequestDtoBuilder.builder();
  }
}
