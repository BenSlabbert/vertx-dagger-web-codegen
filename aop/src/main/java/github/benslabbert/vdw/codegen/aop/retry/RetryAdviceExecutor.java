/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.retry;

import java.util.concurrent.Callable;

public final class RetryAdviceExecutor {

  private RetryAdviceExecutor() {}

  public static Object fixedRetry(Callable<?> callable) {
    try {
      Object call1 = callable.call();
      Object call2 = callable.call();
      return call1;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Object exponentialBackoffRetry(Callable<?> callable) {
    try {
      Object call1 = callable.call();
      Object call2 = callable.call();
      return call1;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
