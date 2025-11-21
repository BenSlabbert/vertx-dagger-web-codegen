/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.aop.cache.CacheAdviceExecutor;
import github.benslabbert.vdw.codegen.aop.cache.CacheKeyBuilder;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.asm.Advice.Thrown;

public final class CachePutAdvice {

  private CachePutAdvice() {}

  @OnMethodExit(inline = false, onThrowable = Throwable.class)
  public static void exit(
      @CacheData.Name String name,
      @CacheData.Key String keyPattern,
      @CacheData.Async boolean async,
      @Return Object returnValue,
      @Thrown Throwable thrown,
      @AllArguments Object[] args) {
    String key = CacheKeyBuilder.buildKey(keyPattern, args);

    if (null == thrown) {
      CacheAdviceExecutor.put(name, key, async, returnValue);
    }
  }
}
