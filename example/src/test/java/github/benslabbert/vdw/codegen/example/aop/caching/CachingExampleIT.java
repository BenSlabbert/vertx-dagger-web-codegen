/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop.caching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import github.benslabbert.vdw.codegen.aop.cache.Cache;
import github.benslabbert.vdw.codegen.aop.cache.CacheAdviceExecutor;
import github.benslabbert.vdw.codegen.aop.cache.CacheManager;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CachingExampleIT {

  private CachingExample cachingExample;
  private CacheManager cacheManager;
  private Cache cache;

  @BeforeEach
  void setUp() {
    cacheManager = Mockito.mock(CacheManager.class);
    cache = Mockito.mock(Cache.class);
    CacheAdviceExecutor.setCacheManager(cacheManager);
    cachingExample = new CachingExample();
  }

  @AfterEach
  void afterEach() {
    CacheAdviceExecutor.clearCacheManager();
  }

  @Test
  void cachedAsync() {
    when(cacheManager.getCache("cache")).thenReturn(Optional.of(cache));
    when(cache.get("k-in")).thenReturn(null);

    String s = cachingExample.cachedAsync("in");
    assertThat(s).isEqualTo("in");

    verify(cache).get("k-in");
    // async invocation
    verify(cache, timeout(250L).times(1)).put("k-in", "in");
    verify(cacheManager, timeout(250L).times(2)).getCache("cache");
    verifyNoMoreInteractions(cacheManager, cache);
  }

  @Test
  void cached() {
    when(cacheManager.getCache("cache")).thenReturn(Optional.of(cache));
    when(cache.get("k-in")).thenReturn("data");

    String s = cachingExample.cached("in");
    assertThat(s).isEqualTo("data");

    verify(cacheManager).getCache("cache");
    verify(cache).get("k-in");
    verifyNoMoreInteractions(cacheManager, cache);
  }

  @Test
  void revoke() {
    when(cacheManager.getCache("cache")).thenReturn(Optional.of(cache));

    cachingExample.revoke();

    verify(cacheManager).getCache("cache");
    verify(cache).evict("key");
    verifyNoMoreInteractions(cacheManager, cache);
  }
}
