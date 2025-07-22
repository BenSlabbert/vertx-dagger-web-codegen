/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

final class PathParser {

  private PathParser() {}

  static ParseResult parse(String path) {
    List<QueryParam> queryParams = List.of();

    int queryStart = path.indexOf('?');
    if (-1 != queryStart) {
      String queryString = path.substring(queryStart + 1);
      path = path.substring(0, queryStart);
      queryParams = parseQueryParams(queryString);
    }

    List<Param> pathParams = parsePathParams(path);
    return new ParseResult(queryParams, pathParams);
  }

  private static List<QueryParam> parseQueryParams(String path) {
    if (StringUtils.isBlank(path)) {
      return List.of();
    }

    List<QueryParam> queryParams = new ArrayList<>();
    String[] strings = path.split("&");

    for (String string : strings) {

      int i = string.indexOf('=');
      if (-1 == i) {
        throw new GenerationException("illegal query parameter: " + string);
      }

      String queryParamName = StringUtils.trimToEmpty(string.substring(0, i));
      List<Param> params = parsePathParams(StringUtils.trimToEmpty(string.substring(i + 1)));
      if (1 != params.size()) {
        throw new GenerationException("illegal query parameter: " + string);
      }
      queryParams.add(new QueryParam(queryParamName, params.getFirst()));
    }

    return queryParams;
  }

  private static List<Param> parsePathParams(String path) {
    Set<String> names = new HashSet<>();
    List<Param> params = new ArrayList<>();

    int idx = path.indexOf('{');
    while (idx != -1) {
      int endIdx = path.indexOf('}');
      String param = path.substring(idx + 1, endIdx);
      String[] split = param.split(":");
      if (2 != split.length) {
        throw new GenerationException("illegal path parameter: " + param);
      }

      String type = StringUtils.trimToEmpty(split[0]).toLowerCase();
      String[] nameAndDefault = split[1].split("=");

      if (nameAndDefault.length > 2) {
        throw new GenerationException("illegal path parameter: " + param);
      }

      String name = nameAndDefault[0];
      String defaultValue = null;
      if (2 == nameAndDefault.length) {
        defaultValue = nameAndDefault[1];
      }

      if (!names.add(name)) {
        throw new GenerationException("duplicate path parameter: " + name);
      }

      params.add(new Param(map(type), name, Optional.ofNullable(defaultValue)));

      path = path.substring(endIdx + 1);
      idx = path.indexOf('{');
    }

    return params;
  }

  private static Type map(String type) {
    return switch (type) {
      case "int" -> Type.INT;
      case "string" -> Type.STRING;
      case "long" -> Type.LONG;
      case "boolean" -> Type.BOOLEAN;
      case "float" -> Type.FLOAT;
      case "double" -> Type.DOUBLE;
      case "ts" -> Type.TIMESTAMP;
      case null, default -> throw new GenerationException("illegal path parameter type: " + type);
    };
  }

  record ParseResult(List<QueryParam> queryParams, List<Param> pathParams) {}

  record Param(Type type, String name, Optional<String> defaultValue) {}

  record QueryParam(String queryParamName, Param param) {}

  enum Type {
    INT,
    LONG,
    BOOLEAN,
    FLOAT,
    DOUBLE,
    TIMESTAMP,
    STRING
  }
}
