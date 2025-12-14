/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.config;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.commons.config.ApplicationConfigBuilder.Builder;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.Nullable;
import java.time.Duration;

@GenerateBuilder
public record ApplicationConfig(
    Profile profile,
    @Nullable HttpConfig httpConfig,
    @Nullable RedisConfig redisConfig,
    @Nullable PostgresConfig postgresConfig,
    @Nullable JdbcConfig jdbcConfig) {

  public static ApplicationConfig fromJson(JsonObject jsonObject) {
    Builder builder = ApplicationConfigBuilder.builder();

    addProfile(jsonObject, builder);
    addHttpConfig(jsonObject, builder);
    addRedisConfig(jsonObject, builder);
    addPostgresConfig(jsonObject, builder);
    addJdbcConfig(jsonObject, builder);

    return builder.build();
  }

  private static void addProfile(JsonObject jsonObject, Builder builder) {
    String profile = jsonObject.getString("profile", null);
    builder.profile(Profile.fromString(profile));
  }

  private static void addHttpConfig(JsonObject jsonObject, Builder builder) {
    JsonObject config = jsonObject.getJsonObject("httpConfig", new JsonObject());

    if (isNullOrEmpty(config)) {
      return;
    }

    HttpConfig httpConfig =
        ApplicationConfig_HttpConfigBuilder.builder().port(config.getInteger("port")).build();
    builder.httpConfig(httpConfig);
  }

  private static void addRedisConfig(JsonObject jsonObject, Builder builder) {
    JsonObject config = jsonObject.getJsonObject("redisConfig", new JsonObject());

    if (isNullOrEmpty(config)) {
      return;
    }

    builder.redisConfig(
        ApplicationConfig_RedisConfigBuilder.builder()
            .host(config.getString("host"))
            .port(config.getInteger("port"))
            .database(config.getInteger("database"))
            .build());
  }

  private static void addPostgresConfig(JsonObject jsonObject, Builder builder) {
    JsonObject config = jsonObject.getJsonObject("postgresConfig", new JsonObject());

    if (isNullOrEmpty(config)) {
      return;
    }

    builder.postgresConfig(
        ApplicationConfig_PostgresConfigBuilder.builder()
            .host(config.getString("host"))
            .port(config.getInteger("port"))
            .database(config.getString("database"))
            .password(config.getString("password"))
            .username(config.getString("username"))
            .schema(config.getString("schema", "public"))
            .build());
  }

  private static void addJdbcConfig(JsonObject jsonObject, Builder builder) {
    JsonObject config = jsonObject.getJsonObject("jdbcConfig", new JsonObject());

    if (isNullOrEmpty(config)) {
      return;
    }

    builder.jdbcConfig(
        ApplicationConfig_JdbcConfigBuilder.builder()
            .fetchSize(config.getInteger("fetchSize"))
            .queryTimeout(Duration.ofSeconds(config.getInteger("queryTimeout")))
            .build());
  }

  private static boolean isNullOrEmpty(JsonObject config) {
    return null == config || config.isEmpty();
  }

  public enum Profile {
    DEV,
    PROD;

    static Profile fromString(String value) {
      if (null != value) {
        value = value.toLowerCase();
      }

      return switch (value) {
        case "dev" -> DEV;
        case "prod" -> PROD;
        case null -> PROD;
        default -> throw new IllegalArgumentException("Invalid profile: " + value);
      };
    }
  }

  @GenerateBuilder
  public record HttpConfig(int port) {}

  @GenerateBuilder
  public record RedisConfig(String host, int port, int database) {

    public String uri() {
      return String.format("redis://%s:%d/%d", host, port, database);
    }
  }

  @GenerateBuilder
  public record PostgresConfig(
      String host,
      int port,
      String username,
      String password,
      String database,
      @Nullable String schema) {

    public PostgresConfig {
      if (null == schema) {
        schema = "public";
      }
    }

    public String uri() {
      return String.format(
          "jdbc:postgresql://%s:%d/%s?currentSchema=%s", host, port, database, schema);
    }
  }

  @GenerateBuilder
  public record JdbcConfig(int fetchSize, Duration queryTimeout) {}
}
