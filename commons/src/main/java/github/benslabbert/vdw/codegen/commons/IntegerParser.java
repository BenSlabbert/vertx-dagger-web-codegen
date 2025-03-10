/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons;

import org.apache.commons.lang3.math.NumberUtils;

public final class IntegerParser implements Parser<Integer> {

  private IntegerParser() {}

  public static IntegerParser create() {
    return new IntegerParser();
  }

  @Override
  public Integer parse(String value) {
    return NumberUtils.isCreatable(value) ? NumberUtils.createInteger(value) : null;
  }

  @Override
  public Integer parse(String value, Integer defaultValue) {
    return NumberUtils.isCreatable(value) ? NumberUtils.createInteger(value) : defaultValue;
  }
}
