/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Headers;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/headers")
class HeadersHandler {

  private static final Logger log = LoggerFactory.getLogger(HeadersHandler.class);

  @Inject
  HeadersHandler() {}

  @Get
  Buffer get(@Headers MultiMap headers) {
    log.info("headers size {}", headers.size());
    return Buffer.buffer("headers");
  }
}
