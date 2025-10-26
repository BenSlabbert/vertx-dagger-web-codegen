/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import dagger.BindsInstance;
import dagger.Component;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcQueryRunnerFactory;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcTransactionManager;
import jakarta.inject.Singleton;
import javax.sql.DataSource;

@Singleton
@Component(modules = ModuleBindings.class)
interface Provider {

  JdbcTransactionManager jdbcTransactionManager();

  JdbcQueryRunnerFactory jdbcQueryRunnerFactory();

  PersonRepository personRepository();

  AddressRepository addressRepository();

  DataSource dataSource();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder dataSource(DataSource dataSource);

    Provider build();
  }
}
