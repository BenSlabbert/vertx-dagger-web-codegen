/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.auth.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.eb.EventBusService;
import io.vertx.core.Future;

@NoAuthCheck
@EventBusService(address = "ADDR")
public interface ExampleServiceNoAuthNoValidator {

  Future<DataResponse> getData(DataRequest request);

  Future<MetaResponse> getMeta(DataRequest request);
}
