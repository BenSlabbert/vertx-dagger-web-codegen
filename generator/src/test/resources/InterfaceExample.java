/* Licensed under Apache-2.0 2025. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonWriter
public interface InterfaceExample {
  @NotBlank @Size(min = 1, max = 100) String name();

  @Nullable Integer age();

  boolean active();
}

// Package-private test builder class. This is intentionally not used at runtime.
// The annotation processor only analyzes the interface structure for code generation.
class InterfaceExampleBuilder {

  public static Builder builder() {
    // Note: This is a test resource file used only for compilation testing.
    // The annotation processor only reads the interface structure, not the builder implementation.
    // In production code, a proper builder would be provided.
    throw new UnsupportedOperationException("Test builder placeholder");
  }

  public interface Builder {
    Builder name(String name);

    Builder age(Integer age);

    Builder active(boolean active);

    InterfaceExample build();
  }
}
