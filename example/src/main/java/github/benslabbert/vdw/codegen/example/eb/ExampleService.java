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

@EventBusService(address = "example.address.my")
interface ExampleService {

  @HasRole("admin")
  Future<ExampleResponse> execute(@Valid ExampleRequest request);

  record ExampleRequest(String data) {

    JsonObject toJson() {
      return new JsonObject().put("data", data);
    }

    static ExampleRequest fromJson(JsonObject json) {
      return new ExampleRequest(json.getString("data"));
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

  record ExampleResponse(String data) {

    JsonObject toJson() {
      return new JsonObject().put("data", data);
    }

    static ExampleResponse fromJson(JsonObject json) {
      return new ExampleResponse(json.getString("data"));
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
}
