/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import io.vertx.core.Future;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
class Impl implements DataServiceNoAuthNoValidation, DataServiceNoRoles, DataServiceNoValidation {

  @Inject
  Impl() {}

  @Override
  public Future<DataResponse> getData(DataRequest request) {
    return null;
  }

  @Override
  public Future<MetaResponse> getMeta(DataRequest request) {
    return null;
  }
}
