/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import github.benslabbert.vertxjsonwriter.annotation.JsonWriter;

@JsonWriter
@GenerateBuilder
public record MetaResponse(String data) {

  public static MetaResponseBuilder.Builder builder() {
    return MetaResponseBuilder.builder();
  }
}
