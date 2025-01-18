/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import org.mapstruct.Mapper;

@Mapper(config = MapperBaseConfig.class)
interface ResponseDtoMapper {

  ResponseDto map(RequestDto dto);
}
