/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons.jdbc;

public class JdbcTransactionException extends RuntimeException {
  public JdbcTransactionException(Throwable cause) {
    super(cause);
  }
}
