/* Licensed under Apache-2.0 2026. */
package github.benslabbert.vdw.codegen.example.json;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

@JsonWriter
@GenerateBuilder
public record Example(
    @NotBlank @Size(min = 1, max = 10) String name,
    @Min(1) @Max(10) Integer value,
    LocalDate date,
    LocalDateTime time,
    OffsetDateTime offsetDateTime,
    Set<@NotNull @NotBlank @Size(min = 2) String> tags) {}
