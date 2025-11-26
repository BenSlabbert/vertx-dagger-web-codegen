/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import github.benslabbert.vdw.codegen.annotation.HasRole;
import io.vertx.core.Future;
import jakarta.validation.Valid;

@EventBusService(address = "ADDR")
public interface ExampleService {

  @HasRole("role-1")
  Future<DataResponse> getData(@Valid DataRequest request);

  @HasRole({"role-1", "role-2"})
  Future<MetaResponse> getMeta(DataRequest request);
}
