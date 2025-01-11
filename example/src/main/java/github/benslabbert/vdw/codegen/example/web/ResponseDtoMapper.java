/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.web;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
interface ResponseDtoMapper {

  ResponseDto map(RequestDto dto);
}
