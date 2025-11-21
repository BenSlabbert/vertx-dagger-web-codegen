/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.cache;

public interface Cache {

  Object get(String key);

  void put(String key, Object value);

  void evict(String key);
}
