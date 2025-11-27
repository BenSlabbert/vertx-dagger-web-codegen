/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import github.benslabbert.vdw.codegen.annotation.eb.EventBusService;
import io.vertx.core.Future;

@EventBusService(address = "ADDR-2")
public interface DataServiceNoRoles {

  Future<DataResponse> getData(DataRequest request);

  Future<MetaResponse> getMeta(DataRequest request);
}
