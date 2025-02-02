/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.WebRequest.UserContext;
import io.vertx.core.buffer.Buffer;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/user")
class UserContextHandler {

  private static final Logger log = LoggerFactory.getLogger(UserContextHandler.class);

  @Inject
  UserContextHandler() {}

  @Get
  Buffer get(@UserContext io.vertx.ext.web.UserContext userContext) {
    log.info("userContext {}", userContext);
    return Buffer.buffer("headers");
  }
}
