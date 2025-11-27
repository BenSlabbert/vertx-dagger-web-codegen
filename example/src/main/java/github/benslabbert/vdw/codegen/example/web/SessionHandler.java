/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
import io.vertx.ext.web.Session;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/session")
class SessionHandler {

  private static final Logger log = LoggerFactory.getLogger(SessionHandler.class);

  @Inject
  SessionHandler() {}

  @Get
  void get(@WebRequest.Session Session session) {
    log.info("session id {}", session.id());
  }
}
