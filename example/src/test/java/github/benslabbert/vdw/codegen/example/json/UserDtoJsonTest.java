/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.json;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

class UserDtoJsonTest {

  @Test
  void toJsonSerializesOnlyInterfaceProperties() {
    UserDto userDto =
        new UserDto() {
          @Override
          public String username() {
            return "ben";
          }

          @Override
          public String email() {
            return "ben@example.com";
          }

          @Override
          public boolean active() {
            return true;
          }
        };

    JsonObject json = UserDtoJson.toJson(userDto);

    assertThat(json.fieldNames()).containsExactlyInAnyOrder("username", "email", "active");
    assertThat(json)
        .isEqualTo(
            new JsonObject()
                .put("username", "ben")
                .put("email", "ben@example.com")
                .put("active", true));
  }

  @Test
  void nullable() {
    UserDto userDto =
        new UserDto() {
          @Override
          public String username() {
            return "ben";
          }

          @Override
          public String email() {
            return null;
          }

          @Override
          public boolean active() {
            return true;
          }
        };

    JsonObject json = UserDtoJson.toJson(userDto);

    assertThat(json.fieldNames()).containsExactlyInAnyOrder("username", "active");
    assertThat(json).isEqualTo(new JsonObject().put("username", "ben").put("active", true));
  }
}
