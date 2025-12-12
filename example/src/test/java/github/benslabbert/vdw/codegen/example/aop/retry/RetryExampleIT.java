/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop.retry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import github.benslabbert.vdw.codegen.aop.retry.Retry;
import github.benslabbert.vdw.codegen.aop.retry.RetryAdviceExecutor;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RetryExampleIT {

  private Retry fixedRetry;
  private Retry exponentialRetry;
  private RetryExample retry;

  @BeforeEach
  void before() {
    fixedRetry = Mockito.mock(Retry.class);
    exponentialRetry = Mockito.mock(Retry.class);
    RetryAdviceExecutor.setFixedRetry(fixedRetry);
    RetryAdviceExecutor.setExponentialBackoffRetry(exponentialRetry);
    retry = new RetryExample();
  }

  @AfterEach
  void after() {
    RetryAdviceExecutor.clearFixedRetry();
    RetryAdviceExecutor.clearExponentialBackoffRetry();
  }

  @Test
  void fixed() {
    when(fixedRetry.retry(any(Callable.class))).thenReturn(Retry.Result.success(null));
    retry.fixedDelay();
    verify(fixedRetry).retry(any(Callable.class));
    verifyNoInteractions(exponentialRetry);
  }

  @Test
  void exponential() {
    when(exponentialRetry.retry(any(Callable.class))).thenReturn(Retry.Result.success(null));
    retry.exponentialRetry();
    verify(exponentialRetry).retry(any(Callable.class));
    verifyNoInteractions(fixedRetry);
  }
}
