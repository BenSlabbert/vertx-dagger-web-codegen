/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Example {
  private static final Logger log = LoggerFactory.getLogger(Example.class);

  @CustomAdvice
  void example() {
    log.info("example");
  }
}
