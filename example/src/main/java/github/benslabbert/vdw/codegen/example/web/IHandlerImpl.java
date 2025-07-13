/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
class IHandlerImpl implements IHandler {

  private final ResponseDtoMapper mapper;

  @Inject
  IHandlerImpl(ResponseDtoMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Buffer buffer() {
    return Buffer.buffer("Hello World");
  }

  @Override
  public void ctx(RoutingContext ctx) {
    ctx.end();
  }

  @Override
  public ResponseDto data(RequestDto dto) {
    return mapper.map(dto);
  }

  @Override
  public Future<ResponseDto> future(RequestDto dto) {
    return Future.succeededFuture(mapper.map(dto));
  }
}
