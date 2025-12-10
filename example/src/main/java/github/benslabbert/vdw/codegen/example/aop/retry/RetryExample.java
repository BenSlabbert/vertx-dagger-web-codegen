/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop.retry;

import github.benslabbert.vdw.codegen.annotation.advice.Retryable.ExponentialBackoff;
import github.benslabbert.vdw.codegen.annotation.advice.Retryable.FixedDelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryExample {

  private static final Logger log = LoggerFactory.getLogger(RetryExample.class);

  public static void main(String[] args) {
    RetryExample retry = new RetryExample();
    log.info("before");
    var s = retry.fixedDelay();
    log.info("after tryMe={}", s);
    log.info("before");
    retry.exponentialRetry();
  }

  @FixedDelay
  public String fixedDelay() {
    log.info("tryMe");
    return "hello";
  }

  @ExponentialBackoff
  public void exponentialRetry() {
    log.info("tryMeNow");
  }
}
