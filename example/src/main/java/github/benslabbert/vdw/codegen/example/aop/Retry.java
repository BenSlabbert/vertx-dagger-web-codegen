/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop;

import github.benslabbert.vdw.codegen.annotation.Retryable.ExponentialBackoff;
import github.benslabbert.vdw.codegen.annotation.Retryable.FixedDelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Retry {

  private static final Logger log = LoggerFactory.getLogger(Retry.class);

  static void main() {
    Retry retry = new Retry();
    log.info("before");
    retry.tryMe();
    log.info("before");
    retry.tryMeNow();
  }

  @FixedDelay
  public String tryMe() {
    log.info("tryMe");
    return "hello";
  }

  @ExponentialBackoff
  public void tryMeNow() {
    log.info("tryMeNow");
  }
}
