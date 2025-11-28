/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.di;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import github.benslabbert.vdw.codegen.commons.eb.EventBusServiceConfigurer;
import github.benslabbert.vdw.codegen.example.config.ConfigModule;
import github.benslabbert.vdw.codegen.example.eb.EBModule;
import github.benslabbert.vdw.codegen.example.web.ExampleModule;
import github.benslabbert.vdw.codegen.example.web.RouterFactory;
import github.benslabbert.vdw.codegen.example.web.ServerFactory;
import github.benslabbert.vdw.codegen.txmanager.PlatformTransactionManager;
import github.benslabbert.vdw.codegen.txmanager.TransactionManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHandler;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Component(
    modules = {Provider.EagerModule.class, ExampleModule.class, ConfigModule.class, EBModule.class})
public interface Provider {

  Logger log = LoggerFactory.getLogger(Provider.class);

  RouterFactory routerFactory();

  ServerFactory serverFactory();

  Set<EventBusServiceConfigurer> eventBusServiceConfigurers();

  Set<ProxyHandler> proxyHandlers();

  @Nullable Void init();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder vertx(Vertx vertx);

    @BindsInstance
    Builder config(JsonObject config);

    Provider build();
  }

  @Module
  final class EagerModule {

    @Inject
    EagerModule() {}

    @Provides
    @Nullable static Void provideEager(TransactionManager transactionManager, Vertx vertx) {
      log.info("eager init");
      PlatformTransactionManager.setTransactionManager(transactionManager);
      return null;
    }
  }
}
