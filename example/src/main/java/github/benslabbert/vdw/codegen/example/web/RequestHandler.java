/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Request;
import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/request")
class RequestHandler {

  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  @Inject
  RequestHandler() {}

  @Get(responseCode = 204)
  void get(@Request HttpServerRequest request) {
    log.info("get request cookieCount {}", request.cookieCount());
  }
}
