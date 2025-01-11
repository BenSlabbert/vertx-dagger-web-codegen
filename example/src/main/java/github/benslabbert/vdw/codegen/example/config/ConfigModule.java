/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.config;

import dagger.Module;

@Module(
    includes = {
      ConfigModuleBindings.class,
      SessionHandlerProvider.class,
      SessionStoreProvider.class
    })
public interface ConfigModule {}
