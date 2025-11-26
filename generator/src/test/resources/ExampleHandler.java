/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.HasRole;
import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest;
import github.benslabbert.vdw.codegen.annotation.WebRequest.All;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Delete;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Head;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Headers;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Options;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Patch;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Put;
import github.benslabbert.vdw.codegen.annotation.WebRequest.QueryParams;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Request;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Response;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Trace;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.UserContext;
import jakarta.validation.Valid;

@WebHandler(path = "/route")
class ExampleHandler {

  @Get
  Future<Dto> future() {
    return Future.succeededFuture(new Dto("data"));
  }

  @Get
  Buffer buffer() {
    return Buffer.buffer("data");
  }

  @All
  void all(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @Get
  void get(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @Post
  void post(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @Put
  void put(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @Patch
  void patch(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @Delete
  void delete(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @Options
  void options(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @Trace
  void trace(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @Head
  void head(@WebRequest.RoutingContext RoutingContext rc) {
    rc.end();
  }

  @WebRequest.Produces("produce")
  @Get(path = "/produces")
  void produces() {}

  @WebRequest.Consumes("consume")
  @Get(path = "/consumes")
  void consumes() {}

  @WebRequest.Produces("produce")
  @WebRequest.Consumes("consume")
  @Get(path = "/produce/consumes")
  void produceConsumes() {}

  @Get(path = "/p1", responseCode = 204)
  void getVoid() {}

  @Get(path = "/p2")
  String getString() {
    return "";
  }

  @Get(path = "/p3")
  Dto getString(@Body Dto dto) {
    return new Dto("new data");
  }

  @HasRole("role")
  @Get(path = "/p4")
  void getVoidWithRequestBody(@Body Dto dto) {}

  @HasRole({"role1", "role2"})
  @Get(path = "/p4")
  void validBody(@Valid @Body Dto dto) {}

  @Get(path = "/p5")
  void queryParams(@QueryParams MultiMap queryParams) {}

  @Get(path = "/p6", responseCode = 202)
  void ctx(@WebRequest.RoutingContext RoutingContext ctx) {}

  @Get(path = "/p7", responseCode = 190)
  void ctxqueryParams(
      @WebRequest.RoutingContext RoutingContext ctx, @QueryParams MultiMap queryParams) {}

  @Get(path = "/p8", responseCode = 200)
  void queryParamsctx(
      @QueryParams MultiMap queryParams, @WebRequest.RoutingContext RoutingContext ctx) {}

  @Get(path = "/p9", responseCode = 200)
  void headersParamsctx(@Headers MultiMap headers, @WebRequest.RoutingContext RoutingContext ctx) {}

  @Get(path = "/request")
  void request(@Request HttpServerRequest req) {}

  @Get(path = "/response")
  void response(@Response HttpServerResponse resp) {}

  @Get(path = "/userContext")
  void userContext(@WebRequest.UserContext UserContext uc) {}

  @Get(path = "/session")
  void session(@WebRequest.Session Session session) {}
}
