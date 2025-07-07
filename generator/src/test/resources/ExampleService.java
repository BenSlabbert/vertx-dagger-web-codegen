/* Licensed under Apache-2.0 2025. */
package test;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import github.benslabbert.vdw.codegen.annotation.HasRole;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.Validator;
import jakarta.validation.Valid;

@EventBusService(address = "ADDR")
public interface ExampleService {

  @HasRole("role-1")
  Future<DataResponse> getData(@Valid DataRequest request);

  @HasRole("role-1")
  @HasRole("role-2")
  Future<MetaResponse> getMeta(DataRequest request);

  record DataRequest(String data) {

    JsonObject toJson() {
      return new JsonObject().put("data", data);
    }

    static DataRequest fromJson(JsonObject json) {
      return new DataRequest(json.getString("data"));
    }

    static Validator getValidator() {
      return null;
    }
  }

  record DataResponse(String data) {

    JsonObject toJson() {
      return new JsonObject().put("data", data);
    }

    static DataResponse fromJson(JsonObject json) {
      return new DataResponse(json.getString("data"));
    }
  }

  record MetaResponse(String data) {

    JsonObject toJson() {
      return new JsonObject().put("data", data);
    }

    static MetaResponse fromJson(JsonObject json) {
      return new MetaResponse(json.getString("data"));
    }
  }
}
