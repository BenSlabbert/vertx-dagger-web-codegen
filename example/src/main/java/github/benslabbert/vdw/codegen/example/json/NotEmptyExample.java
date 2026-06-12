/* Licensed under Apache-2.0 2026. */
package github.benslabbert.vdw.codegen.example.json;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@JsonWriter
@GenerateBuilder
public record NotEmptyExample(
    @NotEmpty List<String> names, @NotEmpty Set<String> tags, @NotEmpty String value) {}
