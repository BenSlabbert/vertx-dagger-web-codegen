/* Licensed under Apache-2.0 2024. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;

@JsonWriter
public record Nested(String name, Inner inner) {

  @JsonWriter
  public record Inner(String innerName) {}
}

class NestedBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder name(String name);

    Builder inner(Nested.Inner inner);

    Nested build();
  }
}

class Nested_InnerBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder innerName(String innerName);

    Nested.Inner build();
  }
}
