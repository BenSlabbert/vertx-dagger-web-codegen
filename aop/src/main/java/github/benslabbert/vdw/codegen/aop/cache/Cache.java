/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.cache;

public interface Cache {

  void put(String key, Object value);

  void evict(String key);
}
