/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Async;
import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Key;
import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Name;
import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Policy;
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
      @Name String name,
      @Key String keyPattern,
      @Policy Cache.Policy policy,
      @Async boolean async,
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
