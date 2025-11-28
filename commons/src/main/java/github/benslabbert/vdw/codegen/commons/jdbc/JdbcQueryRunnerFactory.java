/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.jdbc;

import dagger.assisted.AssistedFactory;
import org.apache.commons.dbutils.StatementConfiguration;

@AssistedFactory
public interface JdbcQueryRunnerFactory {

  JdbcQueryRunner create(StatementConfiguration statementConfiguration);

  default JdbcQueryRunner create() {
    return create(new StatementConfiguration.Builder().build());
  }
}
