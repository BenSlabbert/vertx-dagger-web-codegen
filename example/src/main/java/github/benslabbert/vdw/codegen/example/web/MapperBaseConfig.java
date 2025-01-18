package github.benslabbert.vdw.codegen.example.web;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.JAKARTA;

import org.mapstruct.MapperConfig;

@MapperConfig(componentModel = JAKARTA, injectionStrategy = CONSTRUCTOR)
interface MapperBaseConfig {}
