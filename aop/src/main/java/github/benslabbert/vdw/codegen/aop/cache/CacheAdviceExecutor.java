/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.cache;

import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheAdviceExecutor {

  private static final NoOpCache NO_OP_CACHE = new NoOpCache();
  private static final CacheManager DEFAULT_CACHE_MANAGER = ignore -> Optional.empty();

  private static final ThreadFactory THREAD_FACTORY =
      Thread.ofVirtual().name("cache-vthread-", 1L).factory();

  private static final AtomicReference<CacheManager> CACHE_MANAGER =
      new AtomicReference<>(DEFAULT_CACHE_MANAGER);

  private CacheAdviceExecutor() {}

  public static void setCacheManager(CacheManager cm) {
    if (!CACHE_MANAGER.compareAndSet(DEFAULT_CACHE_MANAGER, cm)) {
      throw new IllegalStateException("Cache manager already set");
    }
  }

  public static Object get(String cacheName, String key) {
    return CACHE_MANAGER.get().getCache(cacheName).orElse(NO_OP_CACHE).get(key);
  }

  public static void put(String cacheName, String key, boolean async, Object value) {
    if (async) {
      THREAD_FACTORY.newThread(() -> putInternal(cacheName, key, value)).start();
      return;
    }
    putInternal(cacheName, key, value);
  }

  private static void putInternal(String cacheName, String key, Object value) {
    CACHE_MANAGER.get().getCache(cacheName).orElse(NO_OP_CACHE).put(key, value);
  }

  public static void evict(String cacheName, String key, boolean async) {
    if (async) {
      THREAD_FACTORY.newThread(() -> evictInternal(cacheName, key)).start();
      return;
    }
    evictInternal(cacheName, key);
  }

  private static void evictInternal(String cacheName, String key) {
    CACHE_MANAGER.get().getCache(cacheName).orElse(NO_OP_CACHE).evict(key);
  }

  private static final class NoOpCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(NoOpCache.class);

    @Override
    public Object get(String key) {
      log.info("no-op get key {} return null", key);
      return null;
    }

    @Override
    public void put(String key, Object value) {
      log.info("no-op put key {}", key);
    }

    @Override
    public void evict(String key) {
      log.info("no-op evict key {}", key);
    }
  }
}
