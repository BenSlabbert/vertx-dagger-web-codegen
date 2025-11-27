/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.eb.EventBusService;
import io.vertx.core.Future;
import jakarta.validation.Valid;

@EventBusService(address = "example.address.my")
interface ExampleService {

  @HasRole("admin")
  Future<ExampleResponse> execute(@Valid ExampleRequest request);
}
