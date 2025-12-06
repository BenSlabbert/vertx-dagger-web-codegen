/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcQueryRunnerFactory;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcTransactionManager;
import github.benslabbert.vdw.codegen.txmanager.PlatformTransactionManager;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;
import javax.inject.Inject;
import javax.sql.DataSource;

@Singleton
@Component(modules = {ModuleBindings.class, Provider.EagerModule.class})
interface Provider {

  @Nullable Void init();

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

  default void close() {
    try {
      PlatformTransactionManager.close();
    } catch (Exception e) {
      // do nothing
    }
  }

  @Module
  final class EagerModule {

    @Inject
    EagerModule() {}

    @Provides
    @Nullable static Void provideEager(JdbcTransactionManager jdbcTransactionManager, DataSource dataSource) {
      // this eagerly builds any parameters specified and returns nothing
      PlatformTransactionManager.setTransactionManager(jdbcTransactionManager);
      return null;
    }
  }
}
