/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons;

import io.vertx.core.MultiMap;
import java.util.Map;

public class RequestParser {

  private final MultiMap queryParams;
  private final Map<String, String> pathParams;

  private RequestParser(MultiMap queryParams, Map<String, String> pathParams) {
    this.queryParams = queryParams;
    this.pathParams = pathParams;
  }

  public static RequestParser create(MultiMap queryParams, Map<String, String> pathParams) {
    return new RequestParser(queryParams, pathParams);
  }

  public static RequestParser create(MultiMap queryParams) {
    return new RequestParser(queryParams, Map.of());
  }

  public static RequestParser create(Map<String, String> pathParams) {
    return new RequestParser(MultiMap.caseInsensitiveMultiMap(), pathParams);
  }

  public <T> T getQueryParam(String key, Parser<T> parser) {
    String string = queryParams.get(key);
    return parser.parse(string);
  }

  public <T> T getQueryParam(String key, T defaultValue, Parser<T> parser) {
    String string = queryParams.get(key);
    return parser.parse(string, defaultValue);
  }

  public <T> T getPathParam(String key, Parser<T> parser) {
    String string = pathParams.get(key);
    return parser.parse(string);
  }

  public <T> T getPathParam(String key, T defaultValue, Parser<T> parser) {
    String string = pathParams.get(key);
    return parser.parse(string, defaultValue);
  }
}
