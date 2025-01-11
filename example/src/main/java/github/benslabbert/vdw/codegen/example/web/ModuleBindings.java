/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;

@Module
interface ModuleBindings {

  @Binds
  IHandler ihandler(IHandlerImpl impl);

  @Provides
  @Singleton
  static ResponseDtoMapper responseDtoMapper() {
    return new ResponseDtoMapperImpl();
  }
}
