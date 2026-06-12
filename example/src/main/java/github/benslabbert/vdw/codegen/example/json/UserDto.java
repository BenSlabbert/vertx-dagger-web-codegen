/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.json;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.annotation.Nullable;

@JsonWriter
public interface UserDto {
  String username();

  @Nullable String email();

  boolean active();
}
