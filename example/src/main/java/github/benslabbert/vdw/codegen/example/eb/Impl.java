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
  public Future<DataServiceNoAuthNoValidation.DataResponse> getData(
      DataServiceNoAuthNoValidation.DataRequest request) {
    return null;
  }

  @Override
  public Future<DataServiceNoAuthNoValidation.MetaResponse> getMeta(
      DataServiceNoAuthNoValidation.DataRequest request) {
    return null;
  }

  @Override
  public Future<DataServiceNoRoles.DataResponse> getData(DataServiceNoRoles.DataRequest request) {
    return null;
  }

  @Override
  public Future<DataServiceNoRoles.MetaResponse> getMeta(DataServiceNoRoles.DataRequest request) {
    return null;
  }

  @Override
  public Future<DataServiceNoValidation.DataResponse> getData(
      DataServiceNoValidation.DataRequest request) {
    return null;
  }

  @Override
  public Future<DataServiceNoValidation.MetaResponse> getMeta(
      DataServiceNoValidation.DataRequest request) {
    return null;
  }
}
