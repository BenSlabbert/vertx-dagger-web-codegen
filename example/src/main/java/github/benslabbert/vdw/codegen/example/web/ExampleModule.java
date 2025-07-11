/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import dagger.Module;

@Module(
    includes = {
      IHandler_Router_ModuleBindings.class,
      Handler_Router_ModuleBindings.class,
      Example_Router_ModuleBindings.class,
      HeadersHandler_Router_ModuleBindings.class,
      RequestHandler_Router_ModuleBindings.class,
      ResponseHandler_Router_ModuleBindings.class,
      SessionHandler_Router_ModuleBindings.class,
      UserContextHandler_Router_ModuleBindings.class,
      UserHandler_Router_ModuleBindings.class,
      NoAuthHandler_Router_ModuleBindings.class,
      ModuleBindings.class
    })
public interface ExampleModule {}
