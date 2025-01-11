/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import dagger.Module;

@Module(
    includes = {
      IHandler_Router_ModuleBindings.class,
      Handler_Router_ModuleBindings.class,
      ModuleBindings.class
    })
public interface ExampleModule {}
