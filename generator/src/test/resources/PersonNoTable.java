/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.test;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;

@GenerateBuilder
public record PersonNoTable(long id, String name, int version) {}
