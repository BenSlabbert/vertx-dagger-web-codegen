/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import static org.assertj.core.api.Assertions.assertThat;

import github.benslabbert.vdw.codegen.aop.cache.CacheKeyBuilder;
import org.junit.jupiter.api.Test;

class CacheKeyBuilderTest {

  @Test
  void buildKey() {
    String template = "k-#0-#1-#2-#10-#99";
    Object[] args = {"one", "two", "three", "x", "y", "z", "a", "b", "c", "d", "ten"};
    String replace1 = CacheKeyBuilder.buildKey(template, args);
    String replace2 = CacheKeyBuilder.buildKey(template, args);
    assertThat(replace1).isEqualTo("k-one-two-three-ten-#99");
    assertThat(replace2).isEqualTo("k-one-two-three-ten-#99");
  }
}
