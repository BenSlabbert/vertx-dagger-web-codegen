/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/** Shared PostgreSQL container for all JDBC integration tests. */
public abstract class PostgresTestBase {

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
}
