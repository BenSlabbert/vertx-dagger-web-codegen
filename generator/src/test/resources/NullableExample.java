/* Licensed under Apache-2.0 2025. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@JsonWriter
public record NullableExample(
    @Nullable String name,
    @Nullable Integer value,
    @Nullable LocalDate date,
    @Nullable LocalDateTime time,
    @Nullable OffsetDateTime offsetDateTime,
    @Nullable Inner inner) {

  @JsonWriter
  public record Inner(@Nullable String innerName) {}
}

class NullableExampleBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder name(@Nullable String name);

    Builder value(@Nullable Integer value);

    Builder date(@Nullable LocalDate date);

    Builder time(@Nullable LocalDateTime time);

    Builder offsetDateTime(@Nullable OffsetDateTime offsetDateTime);

    Builder inner(@Nullable NullableExample.Inner inner);

    NullableExample build();
  }
}

class NullableExample_InnerBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder innerName(@Nullable String innerName);

    NullableExample.Inner build();
  }
}
