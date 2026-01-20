/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import dagger.Binds;
import dagger.Module;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcTransactionManager;
import github.benslabbert.vdw.codegen.txmanager.TransactionManager;

@Module(includes = GeneratedModuleBindings.class)
interface JdbcModuleBindings {

  @Binds
  TransactionManager bindTransactionManager(JdbcTransactionManager jdbcTransactionManager);
}
