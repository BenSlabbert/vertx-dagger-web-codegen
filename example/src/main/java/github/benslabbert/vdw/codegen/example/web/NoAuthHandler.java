/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest;
import io.vertx.core.buffer.Buffer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@NoAuthCheck
@WebHandler(path = "/no-auth")
class NoAuthHandler {

  @Inject
  NoAuthHandler() {}

  @WebRequest.Get(path = "/buffer")
  Buffer buffer() {
    return Buffer.buffer("data");
  }
}
