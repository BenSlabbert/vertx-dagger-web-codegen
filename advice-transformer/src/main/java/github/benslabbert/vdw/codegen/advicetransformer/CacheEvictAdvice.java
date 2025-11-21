/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.annotation.Cache;
import github.benslabbert.vdw.codegen.aop.cache.CacheAdviceExecutor;
import github.benslabbert.vdw.codegen.aop.cache.CacheKeyBuilder;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Thrown;

public final class CacheEvictAdvice {

  private CacheEvictAdvice() {}

  @OnMethodExit(inline = false, onThrowable = Throwable.class)
  public static void exit(
      @CacheData.Name String name,
      @CacheData.Key String keyPattern,
      @CacheData.Policy Cache.Policy policy,
      @CacheData.Async boolean async,
      @Thrown Throwable thrown,
      @AllArguments Object[] args) {
    String key = CacheKeyBuilder.buildKey(keyPattern, args);

    if (null == thrown) {
      CacheAdviceExecutor.evict(name, key, async);
      return;
    }

    if (Cache.Policy.ALWAYS == policy) {
      CacheAdviceExecutor.evict(name, key, async);
    }
  }
}
