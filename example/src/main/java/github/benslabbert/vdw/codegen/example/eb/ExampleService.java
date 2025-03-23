/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import github.benslabbert.vdw.codegen.annotation.HasRole;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

@EventBusService(address = "example.address.my")
interface ExampleService {

  @HasRole("admin")
  Future<ExampleResponse> execute(ExampleRequest request);

  record ExampleRequest(String data) {

    JsonObject toJson() {
      return new JsonObject().put("data", data);
    }

    static ExampleRequest fromJson(JsonObject json) {
      return new ExampleRequest(json.getString("data"));
    }
  }

  record ExampleResponse(String data) {

    JsonObject toJson() {
      return new JsonObject().put("data", data);
    }

    static ExampleResponse fromJson(JsonObject json) {
      return new ExampleResponse(json.getString("data"));
    }
  }
}
