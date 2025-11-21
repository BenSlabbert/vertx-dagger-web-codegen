/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.aop.retry.RetryAdviceExecutor;
import java.util.concurrent.Callable;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public final class ExponentialBackoffRetryableAdvice {

  private ExponentialBackoffRetryableAdvice() {}

  @RuntimeType
  public static Object exponentialBackoffRetry(@SuperCall Callable<?> callable) {
    return RetryAdviceExecutor.exponentialBackoffRetry(callable);
  }
}
