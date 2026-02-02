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

class InterfaceExampleBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder name(String name);

    Builder age(Integer age);

    Builder active(boolean active);

    InterfaceExample build();
  }
}
