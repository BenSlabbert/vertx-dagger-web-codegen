/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PathSanitizerTest {

  @Test
  void sanitize() {
    String sanitize = PathSanitizer.sanitize("/a/{int:param1=0}/b/c?q={string:name}");
    assertThat(sanitize).isEqualTo("/a/:param1=0/b/c");
  }
}
