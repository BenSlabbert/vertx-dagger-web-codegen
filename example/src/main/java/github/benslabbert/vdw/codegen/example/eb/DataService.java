/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import github.benslabbert.vdw.codegen.annotation.HasRole;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchema;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.OutputFormat;
import io.vertx.json.schema.Validator;
import jakarta.validation.Valid;

@EventBusService(address = "ADDR")
public interface DataService {

  @HasRole("role-1")
  Future<DataResponse> getData(@Valid DataRequest request);

  @HasRole("role-2")
  Future<MetaResponse> getMeta(DataRequest request);

  record DataRequest(String data) {

    JsonObject toJson() {
      return new JsonObject().put("data", data);
    }

    static DataRequest fromJson(JsonObject json) {
      return new DataRequest(json.getString("data"));
    }

    public static Validator getValidator() {
      return Validator.create(
          JsonSchema.of(objectSchema().requiredProperty("data", stringSchema()).toJson()),
          new JsonSchemaOptions()
              .setBaseUri("https://example.com")
              .setDraft(Draft.DRAFT7)
              .setOutputFormat(OutputFormat.Basic));
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
