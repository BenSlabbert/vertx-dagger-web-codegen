/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.auth.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.eb.EventBusService;
import io.vertx.core.Future;
import jakarta.validation.Valid;

@NoAuthCheck
@EventBusService(address = "ADDR")
public interface ExampleServiceNoAuth {

  Future<DataResponse> getData(@Valid DataRequest request);

  Future<MetaResponse> getMeta(DataRequest request);
}
