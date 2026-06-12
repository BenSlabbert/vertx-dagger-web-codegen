/* Licensed under Apache-2.0 2026. */
package github.benslabbert.vdw.codegen.example.json;

import static io.vertx.json.schema.OutputErrorType.MISSING_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.OutputUnit;
import org.junit.jupiter.api.Test;

class NotEmptyExampleTest {

  @Test
  void test() {
    JsonObject json = new JsonObject();
    JsonArray names = new JsonArray();
    json.put("names", names);

    JsonArray tags = new JsonArray();
    json.put("tags", tags);

    json.put("value", "");

    OutputUnit outputUnit = NotEmptyExampleJson.getValidator().validate(json);
    assertThat(outputUnit.getValid()).isFalse();
    assertThat(outputUnit.getErrorType()).isEqualTo(MISSING_VALUE);

    assertThat(outputUnit.getErrors())
        .hasSize(6)
        .satisfiesExactlyInAnyOrder(
            e -> {
              assertThat(e.getInstanceLocation()).isEqualTo("#/names");
              assertThat(e.getError()).isEqualTo("Property \"names\" does not match schema");
            },
            e -> {
              assertThat(e.getInstanceLocation()).isEqualTo("#/names");
              assertThat(e.getError()).isEqualTo("Array has too few items ( + 0 < 1)");
            },
            e -> {
              assertThat(e.getInstanceLocation()).isEqualTo("#/value");
              assertThat(e.getError()).isEqualTo("Property \"value\" does not match schema");
            },
            e -> {
              assertThat(e.getInstanceLocation()).isEqualTo("#/value");
              assertThat(e.getError()).isEqualTo("String is too short (0 < 1)");
            },
            e -> {
              assertThat(e.getInstanceLocation()).isEqualTo("#/tags");
              assertThat(e.getError()).isEqualTo("Property \"tags\" does not match schema");
            },
            e -> {
              assertThat(e.getInstanceLocation()).isEqualTo("#/tags");
              assertThat(e.getError()).isEqualTo("Array has too few items ( + 0 < 1)");
            });
  }
}
