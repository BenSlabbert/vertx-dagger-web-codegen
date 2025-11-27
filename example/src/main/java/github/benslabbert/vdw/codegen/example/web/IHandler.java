/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Produces;
import io.vertx.core.Future;
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

  @Post(path = "/data")
  Future<ResponseDto> future(@Valid @Body RequestDto dto);
}
