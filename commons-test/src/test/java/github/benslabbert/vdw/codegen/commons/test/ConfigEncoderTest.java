/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.test;

import static org.assertj.core.api.Assertions.assertThat;

import github.benslabbert.vdw.codegen.commons.config.ApplicationConfig;
import github.benslabbert.vdw.codegen.commons.config.ApplicationConfigBuilder;
import github.benslabbert.vdw.codegen.commons.config.ApplicationConfig_HttpConfigBuilder;
import github.benslabbert.vdw.codegen.commons.config.ApplicationConfig_JdbcConfigBuilder;
import github.benslabbert.vdw.codegen.commons.config.ApplicationConfig_PostgresConfigBuilder;
import github.benslabbert.vdw.codegen.commons.config.ApplicationConfig_RedisConfigBuilder;
import io.vertx.core.json.JsonObject;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ConfigEncoderTest {

  @Test
  void test() {
    ApplicationConfig config =
        ApplicationConfigBuilder.builder()
            .profile(ApplicationConfig.Profile.PROD)
            .httpConfig(ApplicationConfig_HttpConfigBuilder.builder().port(123).build())
            .redisConfig(
                ApplicationConfig_RedisConfigBuilder.builder()
                    .host("host")
                    .port(456)
                    .database(1)
                    .build())
            .postgresConfig(
                ApplicationConfig_PostgresConfigBuilder.builder()
                    .host("host")
                    .port(1)
                    .username("username")
                    .password("password")
                    .database("database")
                    .build())
            .jdbcConfig(
                ApplicationConfig_JdbcConfigBuilder.builder()
                    .fetchSize(1)
                    .queryTimeout(Duration.ofSeconds(1L))
                    .build())
            .build();

    JsonObject encode = ConfigEncoder.encode(config);
    assertThat(encode.encode())
        .isEqualTo(
            "{\"profile\":\"PROD\",\"httpConfig\":{\"port\":123},\"redisConfig\":{\"host\":\"host\",\"port\":456,\"database\":1},\"postgresConfig\":{\"host\":\"host\",\"port\":1,\"username\":\"username\",\"password\":\"password\",\"database\":\"database\",\"schema\":\"public\"},\"jdbcConfig\":{\"fetchSize\":1,\"queryTimeout\":1.0}}");
  }
}
