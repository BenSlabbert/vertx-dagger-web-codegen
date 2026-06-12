/* Licensed under Apache-2.0 2026. */
package github.benslabbert.vdw.codegen.example.json;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.OutputUnit;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class NullableExampleTest {

  @Test
  void nulls() {
    NullableExample nullableExample = NullableExampleBuilder.builder().build();
    JsonObject json = NullableExampleJson.toJson(nullableExample);
    assertThat(json.fieldNames()).isEmpty();
    assertThat(json.toString()).isEqualTo("{}");
  }

  @Test
  void validate_empty() {
    OutputUnit outputUnit = NullableExampleJson.getValidator().validate(new JsonObject());
    assertThat(outputUnit.getValid()).isTrue();
  }

  @Test
  void non_nulls() {
    OffsetDateTime offsetDateTime =
        OffsetDateTime.of(LocalDate.of(2026, 1, 1), LocalTime.of(1, 2, 3, 4), ZoneOffset.UTC);
    NullableExample nullableExample =
        NullableExampleBuilder.builder()
            .name("name")
            .value(1)
            .date(offsetDateTime.toLocalDate())
            .time(offsetDateTime.toLocalDateTime())
            .offsetDateTime(offsetDateTime)
            .inner(NullableExample_InnerBuilder.builder().innerName("innerName").build())
            .build();
    JsonObject json = NullableExampleJson.toJson(nullableExample);
    assertThat(json.fieldNames())
        .containsExactlyInAnyOrder("name", "value", "date", "time", "offsetDateTime", "inner");

    assertThat(json.toString())
        .isEqualTo(
            "{\"name\":\"name\",\"value\":1,\"date\":\"2026-01-01\",\"time\":\"2026-01-01T01:02:03.000000004\",\"offsetDateTime\":\"2026-01-01T01:02:03.000000004Z\",\"inner\":{\"innerName\":\"innerName\"}}");
  }
}
