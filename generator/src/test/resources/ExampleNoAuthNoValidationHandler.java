/* Licensed under Apache-2.0 2024. */
package test;

import github.benslabbert.vdw.codegen.annotation.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.generator.Dto;
import io.vertx.core.buffer.Buffer;

@NoAuthCheck
@WebHandler(path = "/route")
class ExampleNoAuthHandler {

  @Get
  Buffer buffer() {
    return Buffer.buffer("data");
  }

  @Get(path = "/p4")
  void validBody(@Body Dto dto) {}
}
