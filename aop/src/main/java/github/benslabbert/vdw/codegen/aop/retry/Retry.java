/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.retry;

import java.util.concurrent.Callable;

public interface Retry {

  Result retry(Callable<?> callable);

  record Result(Object result, Exception error) {

    public static Result success(Object result) {
      return new Result(result, null);
    }

    public static Result failure(Exception error) {
      return new Result(null, error);
    }

    public boolean isSuccess() {
      return null == error;
    }
  }
}
