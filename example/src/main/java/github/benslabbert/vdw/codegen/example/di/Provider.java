/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.di;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import github.benslabbert.txmanager.PlatformTransactionManager;
import github.benslabbert.txmanager.TransactionManager;
import github.benslabbert.vdw.codegen.example.config.ConfigModule;
import github.benslabbert.vdw.codegen.example.web.ExampleModule;
import github.benslabbert.vdw.codegen.example.web.RouterFactory;
import github.benslabbert.vdw.codegen.example.web.ServerFactory;
import io.vertx.core.Vertx;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Component(modules = {Provider.EagerModule.class, ExampleModule.class, ConfigModule.class})
public interface Provider {

  Logger log = LoggerFactory.getLogger(Provider.class);

  RouterFactory routerFactory();

  ServerFactory serverFactory();

  @Nullable Void init();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder vertx(Vertx vertx);

    Provider build();
  }

  @Module
  final class EagerModule {

    @Inject
    EagerModule() {}

    @Provides
    @Nullable static Void provideEager() {
      // this eagerly builds any parameters specified and returns nothing
      log.info("eager init");
      PlatformTransactionManager.setTransactionManager(
          new TransactionManager() {
            @Override
            public void begin() {}

            @Override
            public void ensureActive() {}

            @Override
            public void commit() {}

            @Override
            public void rollback() {}

            @Override
            public void close() {}
          });
      return null;
    }
  }
}
