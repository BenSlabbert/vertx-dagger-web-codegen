/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import github.benslabbert.vertxjsonwriter.annotation.JsonWriter;

@JsonWriter
@GenerateBuilder
public record DataResponse(String data) {

  public static DataResponseBuilder.Builder builder() {
    return DataResponseBuilder.builder();
  }
}
