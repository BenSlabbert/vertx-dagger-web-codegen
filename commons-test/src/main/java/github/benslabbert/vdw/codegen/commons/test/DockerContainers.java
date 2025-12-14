/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons.test;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class DockerContainers {

  private DockerContainers() {}

  public static final PostgreSQLContainer POSTGRES =
      new PostgreSQLContainer(
              DockerImageName.parse("docker.io/postgres:17-alpine")
                  .asCompatibleSubstituteFor("postgres"))
          .withDatabaseName("postgres")
          .withPassword("postgres")
          .withUsername("postgres");
}
