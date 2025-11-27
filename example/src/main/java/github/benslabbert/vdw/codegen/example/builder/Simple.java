/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.builder;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;

@GenerateBuilder
record Simple(String data, int number) {}
