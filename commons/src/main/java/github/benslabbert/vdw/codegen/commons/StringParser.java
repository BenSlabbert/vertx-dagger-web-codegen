/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons;

public final class StringParser implements Parser<String> {

  private StringParser() {}

  public static StringParser create() {
    return new StringParser();
  }

  @Override
  public String parse(String value) {
    return value;
  }

  @Override
  public String parse(String value, String defaultValue) {
    return null != value ? value : defaultValue;
  }
}
