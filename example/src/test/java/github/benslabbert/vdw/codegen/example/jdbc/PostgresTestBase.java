/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/** Shared PostgreSQL container for all JDBC integration tests. */
public abstract class PostgresTestBase {

  public static final PostgreSQLContainer POSTGRES =
      new PostgreSQLContainer(
              DockerImageName.parse("docker.io/postgres:17-alpine")
                  .asCompatibleSubstituteFor("postgres"))
          .withDatabaseName("postgres")
          .withPassword("postgres")
          .withUsername("postgres");

  static {
    System.setProperty("org.jooq.no-tips", "true");
    System.setProperty("org.jooq.no-logo", "true");
    POSTGRES.start();
  }

  protected Provider provider;

  @BeforeEach
  void init() {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(POSTGRES.getJdbcUrl());
    hikariConfig.setUsername("postgres");
    hikariConfig.setPassword("postgres");
    hikariConfig.setAutoCommit(false);
    hikariConfig.setMaximumPoolSize(2); // Increased for optimistic locking concurrency tests
    hikariConfig.setPoolName("jdbc");
    hikariConfig.setThreadFactory(Thread.ofVirtual().name("v-", 0L).factory());

    provider = DaggerProvider.builder().dataSource(new HikariDataSource(hikariConfig)).build();
    provider.init();

    try (var c = provider.dataSource().getConnection();
        var s =
            c.prepareStatement(
                """
                 create sequence id_seq
                    start 1
                    increment 1
                    cache 1;

                 create table address (
                    id bigint primary key,
                    version int4 not null,
                    street text,
                    postal_code text
                 );

                 create table person (
                    id bigint primary key,
                    version int4,
                    first_name text,
                    middle_name text,
                    last_name text,
                    age int4,
                    gender text,
                    address_id bigint not null unique,
                    constraint person_address_fk foreign key (address_id) references address(id)
                 );
                """)) {
      s.execute();
      c.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @AfterEach
  void after() {
    try (var c = provider.dataSource().getConnection();
        var s = c.createStatement()) {
      s.execute(
          """
           drop table person;
           drop table address;
           drop sequence id_seq;
          """);
      c.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      provider.close();
    }
  }
}
