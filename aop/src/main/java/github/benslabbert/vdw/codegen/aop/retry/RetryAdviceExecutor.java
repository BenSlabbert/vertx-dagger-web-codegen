/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.retry;

import jakarta.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RetryAdviceExecutor {

  private static final NoOpRetry NO_OP_RETRY = new NoOpRetry();
  private static final AtomicReference<Retry> FIXED_RETRY = new AtomicReference<>(NO_OP_RETRY);
  private static final AtomicReference<Retry> EXPONENTIAL_BACKOFF_RETRY =
      new AtomicReference<>(NO_OP_RETRY);

  private RetryAdviceExecutor() {}

  public static void setFixedRetry(@Nonnull Retry r) {
    Objects.requireNonNull(r);

    if (!FIXED_RETRY.compareAndSet(NO_OP_RETRY, r)) {
      throw new IllegalStateException("fixed retry already set");
    }
  }

  public static void setExponentialBackoffRetry(@Nonnull Retry r) {
    Objects.requireNonNull(r);

    if (!EXPONENTIAL_BACKOFF_RETRY.compareAndSet(NO_OP_RETRY, r)) {
      throw new IllegalStateException("exponential backoff retry already set");
    }
  }

  public static Object fixedRetry(@Nonnull Callable<?> callable) {
    Objects.requireNonNull(callable);

    Retry retry = FIXED_RETRY.get();
    return execute(retry, callable);
  }

  public static Object exponentialBackoffRetry(@Nonnull Callable<?> callable) {
    Objects.requireNonNull(callable);

    Retry retry = EXPONENTIAL_BACKOFF_RETRY.get();
    return execute(retry, callable);
  }

  private static Object execute(Retry retry, Callable<?> callable) {
    Retry.Result result = retry.retry(callable);
    if (result.isSuccess()) {
      return result.result();
    }

    throw new RetryException(result.error());
  }

  private static final class NoOpRetry implements Retry {

    private static final Logger log = LoggerFactory.getLogger(NoOpRetry.class);

    @Override
    public Result retry(Callable<?> callable) {
      log.debug("NoOp retry");
      try {
        Object call = callable.call();
        return Result.success(call);
      } catch (Exception e) {
        return Result.failure(e);
      }
    }
  }
}
