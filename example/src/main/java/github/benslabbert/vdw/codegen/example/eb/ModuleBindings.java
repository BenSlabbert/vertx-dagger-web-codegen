/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import dagger.Binds;
import dagger.Module;

@Module
interface ModuleBindings {

  @Binds
  DataService bindDataService(EventBusServiceImpl service);

  @Binds
  ExampleService bindExampleService(ExampleServiceImpl service);
}
