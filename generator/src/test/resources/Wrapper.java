/* Licensed under Apache-2.0 2024. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;

@JsonWriter
public record Wrapper(
    Integer number, Boolean bool, Float fl, Double dub, Short sh, Character ch, Byte b, Long l) {}

class WrapperBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder number(Integer number);

    Builder bool(Boolean bool);

    Builder fl(Float fl);

    Builder dub(Double dub);

    Builder sh(Short sh);

    Builder ch(Character ch);

    Builder b(Byte b);

    Builder l(Long l);

    Wrapper build();
  }
}
