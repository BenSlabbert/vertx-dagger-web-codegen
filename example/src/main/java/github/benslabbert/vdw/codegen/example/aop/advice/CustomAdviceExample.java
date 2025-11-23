/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CustomAdviceExample {

  private static final Logger log = LoggerFactory.getLogger(CustomAdviceExample.class);

  @CustomAdvice
  void example() {
    log.info("example");
  }
}
