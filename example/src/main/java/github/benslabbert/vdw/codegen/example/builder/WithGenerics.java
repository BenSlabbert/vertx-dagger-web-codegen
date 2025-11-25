/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.builder;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import java.util.List;

@GenerateBuilder
record WithGenerics(List<String> data) {

  static {
    WithGenerics build = WithGenericsBuilder.builder().data(List.of()).build();
    System.err.println(build);
  }
}
