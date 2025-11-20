/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.cache;

import java.util.Optional;

public interface CacheManager {

  Optional<Cache> getCache(String cacheName);
}
