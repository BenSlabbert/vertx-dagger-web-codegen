/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.HasRole;
import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Consumes;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.WebRequest.PathParams;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Produces;
import github.benslabbert.vdw.codegen.annotation.WebRequest.QueryParams;
import github.benslabbert.vdw.codegen.example.service.HandlerService;
import github.benslabbert.vdw.codegen.example.web.Handler_Both_ParamParser.Handler_Both_Params;
import github.benslabbert.vdw.codegen.example.web.Handler_Path_ParamParser.Handler_Path_Params;
import github.benslabbert.vdw.codegen.example.web.Handler_Query_ParamParser.Handler_Query_Params;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import jakarta.inject.Inject;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/handler")
class Handler {

  private static final Logger log = LoggerFactory.getLogger(Handler.class);

  private final HandlerService handlerService;

  @Inject
  Handler(HandlerService handlerService) {
    this.handlerService = handlerService;
    log.info("init Handler");
  }

  @HasRole("admin")
  @Consumes("*")
  @Produces("text/plain")
  @Get(path = "/buffer")
  Buffer buffer() {
    handlerService.process();
    return Buffer.buffer("data");
  }

  @Get(path = "/path?q={string:s}")
  void query(@QueryParams MultiMap queryParams) {
    Handler_Query_Params params = Handler_Query_ParamParser.parse(queryParams);
    String s = params.s();
    log.info("query params: {}", s);
  }

  @Get(path = "/path/{int:param1=0}/path/{string:param2}")
  void path(@PathParams Map<String, String> pathParams) {
    Handler_Path_Params parse = Handler_Path_ParamParser.parse(pathParams);
    int i = parse.param1();
    String s = parse.param2();
    log.info("path params i: {} s: {}", i, s);
  }

  @Get(path = "/some/prefix/{int:param1}/path/{string:param2}/more-path?q={string:q=s}")
  void both(@PathParams Map<String, String> pathParams, @QueryParams MultiMap queryParams) {
    Handler_Both_Params params = Handler_Both_ParamParser.parse(queryParams, pathParams);
    int i = params.param1();
    String s = params.param2();
    String q = params.q();
    log.info("param1={}, param2={}, q={}", i, s, q);
  }
}
