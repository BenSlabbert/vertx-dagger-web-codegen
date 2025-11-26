/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import github.benslabbert.vdw.codegen.annotation.NoAuthCheck;
import io.vertx.core.Future;

@NoAuthCheck
@EventBusService(address = "ADDR-2")
public interface DataServiceNoAuthNoValidation {

  Future<DataResponse> getData(DataRequest request);

  Future<MetaResponse> getMeta(DataRequest request);
}
