/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import github.benslabbert.vertxjsonwriter.annotation.JsonWriter;

@JsonWriter
@GenerateBuilder
public record ExampleRequest(String data) {

  public static ExampleRequestBuilder.Builder builder() {
    return ExampleRequestBuilder.builder();
  }
}
