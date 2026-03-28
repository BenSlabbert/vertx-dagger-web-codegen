/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import github.benslabbert.vdw.codegen.commons.test.DockerContainers;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.postgresql.PostgreSQLContainer;

public abstract class PostgresTestBase {

  public static final PostgreSQLContainer POSTGRES = DockerContainers.POSTGRES;

  static {
    System.setProperty("org.jooq.no-tips", "true");
    System.setProperty("org.jooq.no-logo", "true");
    POSTGRES.start();
  }

  private static HikariDataSource sharedDataSource;
  protected static Provider provider;

  @BeforeAll
  static void initAll() {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(POSTGRES.getJdbcUrl());
    hikariConfig.setUsername("postgres");
    hikariConfig.setPassword("postgres");
    hikariConfig.setAutoCommit(false);
    hikariConfig.setMaximumPoolSize(2);
    hikariConfig.setPoolName("jdbc");
    hikariConfig.setThreadFactory(Thread.ofVirtual().name("v-", 0L).factory());

    sharedDataSource = new HikariDataSource(hikariConfig);
    provider = DaggerProvider.builder().dataSource(sharedDataSource).build();
    provider.init();
  }

  @AfterAll
  static void tearDownAll() {
    provider.close();
    sharedDataSource.close();
  }

  @BeforeEach
  void init() {
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
    }
  }
}
