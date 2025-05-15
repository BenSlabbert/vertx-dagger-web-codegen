/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import java.io.StringWriter;

final class StringWriterFactory {

  private StringWriterFactory() {
    /* */
  }

  static StringWriter create() {
    // 8k should be big enough for most files without needing a resize
    return new StringWriter(8 * 1024);
  }
}
