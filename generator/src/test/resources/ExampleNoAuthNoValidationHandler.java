/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.auth.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
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
