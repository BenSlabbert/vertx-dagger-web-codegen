/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import github.benslabbert.vertxjsonwriter.annotation.JsonWriter;
import jakarta.annotation.Nullable;

@JsonWriter
@GenerateBuilder
public record ResponseDto(@Nullable String data) {

  public static ResponseDtoBuilder.Builder builder() {
    return ResponseDtoBuilder.builder();
  }
}
