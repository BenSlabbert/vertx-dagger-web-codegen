/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.auth.User;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/user")
class UserHandler {

  private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

  @Inject
  UserHandler() {}

  @Get
  Buffer get(@WebRequest.User User user) {
    log.info("user {}", user);
    return Buffer.buffer("headers");
  }
}
