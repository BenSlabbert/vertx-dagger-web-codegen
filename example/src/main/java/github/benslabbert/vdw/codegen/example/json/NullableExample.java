/* Licensed under Apache-2.0 2026. */
package github.benslabbert.vdw.codegen.example.json;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@JsonWriter
@GenerateBuilder
public record NullableExample(
    @Nullable String name,
    @Nullable Integer value,
    @Nullable LocalDate date,
    @Nullable LocalDateTime time,
    @Nullable OffsetDateTime offsetDateTime,
    @Nullable Inner inner) {

  @JsonWriter
  @GenerateBuilder
  public record Inner(@Nullable String innerName) {}
}
