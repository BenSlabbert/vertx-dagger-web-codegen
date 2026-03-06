/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonWriter
public interface UserDto {
  @NotBlank @Size(min = 1, max = 100) String username();

  @Nullable String email();

  boolean active();
}
