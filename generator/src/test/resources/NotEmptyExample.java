/* Licensed under Apache-2.0 2025. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@JsonWriter
public record NotEmptyExample(
    @NotEmpty List<String> names, @NotEmpty Set<String> tags, @NotEmpty String value) {}

class NotEmptyExampleBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder names(List<String> names);

    Builder tags(Set<String> tags);

    Builder value(String value);

    NotEmptyExample build();
  }
}
