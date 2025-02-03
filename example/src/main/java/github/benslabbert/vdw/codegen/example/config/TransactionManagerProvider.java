/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.config;

import dagger.Module;
import dagger.Provides;
import github.benslabbert.txmanager.TransactionManager;
import jakarta.inject.Singleton;
import java.sql.Connection;

@Module
class TransactionManagerProvider {

  private TransactionManagerProvider() {}

  @Provides
  @Singleton
  static TransactionManager transactionManager() {
    return new TransactionManager() {
      @Override
      public Connection getConnection() {
        return null;
      }

      @Override
      public void begin() {}

      @Override
      public void ensureActive() {}

      @Override
      public void commit() {}

      @Override
      public void beforeCommit(Runnable runnable) {}

      @Override
      public void afterCommit(Runnable runnable) {}

      @Override
      public void rollback() {}

      @Override
      public void close() {}
    };
  }
}
