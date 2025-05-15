/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.commons.jdbc;

public interface Reference<T> {

  long id();

  @SuppressWarnings("unchecked")
  default T referenceType() {
    return (T) this;
  }

  default boolean isNew() {
    return 0 == id();
  }
}
