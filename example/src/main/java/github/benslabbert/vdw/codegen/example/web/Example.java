/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

@WebHandler(path = "/example")
class Example {

  private final ResponseDtoMapper mapper;

  @Inject
  Example(ResponseDtoMapper mapper) {
    this.mapper = mapper;
  }

  @Post(path = "/valid-body")
  ResponseDto validBody(@Valid @Body RequestDto req) {
    return new ResponseDto(req.data());
  }

  @Post(path = "/body")
  ResponseDto body(@Body RequestDto req) {
    return mapper.map(req);
  }
}
