/* Licensed under Apache-2.0 2026. */
package github.benslabbert.vdw.codegen.commons.hash;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public final class Murmur3 {

  private Murmur3() {}

  public static long hash(String input) {
    return Hashing.murmur3_128(0).hashString(input, StandardCharsets.UTF_8).asLong();
  }
}
