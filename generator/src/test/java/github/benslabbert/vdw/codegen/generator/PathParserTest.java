/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import static org.assertj.core.api.Assertions.assertThat;

import github.benslabbert.vdw.codegen.generator.PathParser.Param;
import github.benslabbert.vdw.codegen.generator.PathParser.ParseResult;
import github.benslabbert.vdw.codegen.generator.PathParser.QueryParam;
import github.benslabbert.vdw.codegen.generator.PathParser.Type;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PathParserTest {

  @Test
  void parse() {
    ParseResult parse =
        PathParser.parse("/a/{int:param1=0}/b/c?q={string:name}&param_1={int:p1=1}");
    List<Param> pathParams = parse.pathParams();
    List<QueryParam> queryParams = parse.queryParams();

    assertThat(pathParams)
        .singleElement()
        .satisfies(
            p -> {
              assertThat(p.name()).isEqualTo("param1");
              assertThat(p.type()).isEqualTo(Type.INT);
              assertThat(p.defaultValue()).contains("0");
            });

    assertThat(queryParams)
        .hasSize(2)
        .satisfiesExactly(
            q -> {
              assertThat(q.queryParamName()).isEqualTo("q");
              assertThat(q.param())
                  .usingRecursiveComparison()
                  .isEqualTo(new Param(Type.STRING, "name", Optional.empty()));
            },
            q -> {
              assertThat(q.queryParamName()).isEqualTo("param_1");
              assertThat(q.param())
                  .usingRecursiveComparison()
                  .isEqualTo(new Param(Type.INT, "p1", Optional.of("1")));
            });
  }
}
