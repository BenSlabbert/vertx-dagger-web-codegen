/* Licensed under Apache-2.0 2025. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.annotation.Nullable;

@JsonWriter
public interface InterfaceExample extends BaseInterface {
  static InterfaceExample helper() {
    return null;
  }

  String name();

  @Nullable Integer age();

  boolean active();
}

interface BaseInterface {
  String id();
}
