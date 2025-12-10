/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop.cache;

import jakarta.annotation.Nonnull;
import java.util.Objects;
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
  private static final Logger log = LoggerFactory.getLogger(CacheAdviceExecutor.class);

  private CacheAdviceExecutor() {}

  public static void setCacheManager(@Nonnull CacheManager cm) {
    Objects.requireNonNull(cm, "Cache manager cannot be null");
    if (!CACHE_MANAGER.compareAndSet(DEFAULT_CACHE_MANAGER, cm)) {
      throw new IllegalStateException("Cache manager already set");
    }
  }

  public static void clearCacheManager() {
    if (!CACHE_MANAGER.compareAndSet(CACHE_MANAGER.get(), DEFAULT_CACHE_MANAGER)) {
      throw new IllegalStateException("Cache manager already cleared");
    }
  }

  public static Object get(String cacheName, String key) {
    Cache cache = CACHE_MANAGER.get().getCache(cacheName).orElse(NO_OP_CACHE);
    Object value = cache.get(key);
    log.debug("cache get: {} returns: {}", key, value);
    return value;
  }

  public static void put(String cacheName, String key, boolean async, Object value) {
    if (async) {
      log.debug("cache put async: {} returns: {}", key, value);
      THREAD_FACTORY.newThread(() -> putInternal(cacheName, key, value)).start();
      return;
    }
    log.debug("cache put: {} returns: {}", key, value);
    putInternal(cacheName, key, value);
  }

  private static void putInternal(String cacheName, String key, Object value) {
    CACHE_MANAGER.get().getCache(cacheName).orElse(NO_OP_CACHE).put(key, value);
  }

  public static void evict(String cacheName, String key, boolean async) {
    if (async) {
      log.debug("cache evict async: {}", key);
      THREAD_FACTORY.newThread(() -> evictInternal(cacheName, key)).start();
      return;
    }
    log.debug("cache evict: {}", key);
    evictInternal(cacheName, key);
  }

  private static void evictInternal(String cacheName, String key) {
    CACHE_MANAGER.get().getCache(cacheName).orElse(NO_OP_CACHE).evict(key);
  }

  private static final class NoOpCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(NoOpCache.class);

    @Override
    public Object get(String key) {
      log.debug("no-op get key {} return null", key);
      return null;
    }

    @Override
    public void put(String key, Object value) {
      log.debug("no-op put key {}", key);
    }

    @Override
    public void evict(String key) {
      log.debug("no-op evict key {}", key);
    }
  }
}
