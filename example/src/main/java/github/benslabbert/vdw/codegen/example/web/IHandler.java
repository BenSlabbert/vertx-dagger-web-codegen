/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Produces;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import jakarta.validation.Valid;

@WebHandler(path = "/i/handler")
interface IHandler {

  @Produces("text/plain")
  @Get(path = "/buffer")
  Buffer buffer();

  @Get(path = "/ctx")
  void ctx(@WebRequest.RoutingContext RoutingContext ctx);

  @Post(path = "/data")
  ResponseDto data(@Valid @Body RequestDto dto);
}
