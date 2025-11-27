/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.eb.EventBusService;
import io.vertx.core.Future;

@EventBusService(address = "ADDR-1")
public interface DataServiceNoValidation {

  @HasRole("role-1")
  Future<DataResponse> getData(DataRequest request);

  @HasRole({"role-1", "role-2"})
  Future<MetaResponse> getMeta(DataRequest request);
}
