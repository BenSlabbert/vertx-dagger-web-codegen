/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

class AddressRepositoryIT {

  public static final Network network = Network.newNetwork();

  public static final GenericContainer<?> POSTGRES =
      new GenericContainer<>(DockerImageName.parse("docker.io/postgres:17-alpine"))
          .withExposedPorts(5432)
          .withNetwork(network)
          .withNetworkAliases("postgres")
          .withEnv("POSTGRES_USER", "postgres")
          .withEnv("POSTGRES_PASSWORD", "postgres")
          .withEnv("POSTGRES_DB", "postgres")
          // must wait twice as the init process also prints this message
          .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2));

  static {
    System.setProperty("org.jooq.no-tips", "true");
    System.setProperty("org.jooq.no-logo", "true");
    POSTGRES.start();
  }

  private Provider provider;

  @BeforeEach
  void init() {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(
        "jdbc:postgresql://localhost:%d/postgres".formatted(POSTGRES.getMappedPort(5432)));
    hikariConfig.setUsername("postgres");
    hikariConfig.setPassword("postgres");
    hikariConfig.setAutoCommit(false);
    hikariConfig.setMaximumPoolSize(1);
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

  @Test
  void save() {
    Address address =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    assertThat(address).isNotNull();
    assertThat(address.id()).isOne();
    assertThat(address.version()).isZero();
    assertThat(address.postalCode()).isEqualTo("pc1");
    assertThat(address.street()).isEqualTo("s1");
  }

  @Test
  void saveAll() {
    Collection<Address> address =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .insertAll(
                            List.of(
                                AddressBuilder.builder().postalCode("pc1").street("s1").build(),
                                AddressBuilder.builder().postalCode("pc2").street("s2").build())));

    assertThat(address)
        .hasSize(2)
        .satisfiesExactly(
            a -> {
              assertThat(a.id()).isOne();
              assertThat(a.version()).isZero();
              assertThat(a.postalCode()).isEqualTo("pc1");
              assertThat(a.street()).isEqualTo("s1");
            },
            a -> {
              assertThat(a.id()).isEqualTo(2L);
              assertThat(a.version()).isZero();
              assertThat(a.postalCode()).isEqualTo("pc2");
              assertThat(a.street()).isEqualTo("s2");
            });

    Iterator<Address> itr = address.iterator();
    Address a1 = itr.next();
    Address a2 = itr.next();

    Collection<Address> updated =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .updateAll(
                            List.of(
                                AddressBuilder.toBuilder(a1)
                                    .postalCode("pc-new-1")
                                    .street("s-new-1")
                                    .build(),
                                AddressBuilder.toBuilder(a2)
                                    .postalCode("pc-new-2")
                                    .street("s-new-2")
                                    .build())));

    assertThat(updated)
        .hasSize(2)
        .satisfiesExactly(
            a -> {
              assertThat(a.id()).isOne();
              assertThat(a.version()).isOne();
              assertThat(a.postalCode()).isEqualTo("pc-new-1");
              // street is immutable
              assertThat(a.street()).isEqualTo("s1");
            },
            a -> {
              assertThat(a.id()).isEqualTo(2L);
              assertThat(a.version()).isOne();
              assertThat(a.postalCode()).isEqualTo("pc-new-2");
              // street is immutable
              assertThat(a.street()).isEqualTo("s2");
            });
  }
}
