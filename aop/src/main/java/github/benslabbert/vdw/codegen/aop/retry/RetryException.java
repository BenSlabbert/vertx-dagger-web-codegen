/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.retry;

public class RetryException extends RuntimeException {
  public RetryException(Throwable cause) {
    super(cause);
  }
}
