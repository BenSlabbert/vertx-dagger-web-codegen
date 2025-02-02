/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Response;
import io.vertx.core.http.HttpServerResponse;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/response")
class ResponseHandler {

  private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

  @Inject
  ResponseHandler() {}

  @Get
  void get(@Response HttpServerResponse response) {
    log.info("response closed ? {}", response.closed());
    response.setStatusCode(200).end();
  }
}
