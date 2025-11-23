/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop.caching;

import github.benslabbert.vdw.codegen.annotation.Cache;
import github.benslabbert.vdw.codegen.aop.cache.CacheAdviceExecutor;
import github.benslabbert.vdw.codegen.aop.cache.CacheManager;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingExample {

  private static final Logger log = LoggerFactory.getLogger(CachingExample.class);

  private static final github.benslabbert.vdw.codegen.aop.cache.Cache C =
      new github.benslabbert.vdw.codegen.aop.cache.Cache() {
        private final Map<String, Object> cache = new ConcurrentHashMap<>();

        @Override
        public Object get(String key) {
          log.debug("Getting cache entry for key {}", key);
          return cache.get(key);
        }

        @Override
        public void put(String key, Object value) {
          log.debug("Putting cache entry for key {}", key);
          cache.put(key, value);
        }

        @Override
        public void evict(String key) {
          log.debug("Evicting cache entry for key {}", key);
          cache.remove(key);
        }
      };

  public static void main(String[] args) throws Exception {
    CacheAdviceExecutor.setCacheManager(
        new CacheManager() {
          private final Map<String, github.benslabbert.vdw.codegen.aop.cache.Cache> cache =
              new ConcurrentHashMap<>(Map.of("cache", C));

          @Override
          public Optional<github.benslabbert.vdw.codegen.aop.cache.Cache> getCache(
              String cacheName) {
            return Optional.ofNullable(cache.get(cacheName));
          }
        });

    CachingExample caching = new CachingExample();
    log.info("before");
    String async1 = caching.cachedAsync("async1");
    log.info("before");
    String cached1 = caching.cached("data");
    log.info("before");
    String cached2 = caching.cached("data");
    log.info("before");
    caching.revoke();
    log.info("before");
    String async2 = caching.cachedAsync("async2");
    String async3 = caching.cachedAsync("async1");

    log.info("wait 1 sec for all threads");
    try {
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw e;
    }
  }

  @Cache.Put(value = "cache", key = "k-#0", async = true)
  public String cachedAsync(String in) {
    log.info("cached async : {}", in);
    return in;
  }

  @Cache.Put(value = "cache", key = "k-#0")
  public String cached(String in) {
    log.info("cached");
    return in;
  }

  @Cache.Evict(value = "cache", key = "k", policy = Cache.Policy.ALWAYS)
  public void revoke() {
    log.info("revoke");
  }
}
