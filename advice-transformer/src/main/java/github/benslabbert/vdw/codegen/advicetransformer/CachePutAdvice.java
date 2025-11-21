/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Async;
import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Key;
import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Name;
import github.benslabbert.vdw.codegen.aop.cache.CacheAdviceExecutor;
import github.benslabbert.vdw.codegen.aop.cache.CacheKeyBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.asm.Advice.Thrown;

public final class CachePutAdvice {

  private CachePutAdvice() {}

  @OnMethodEnter(inline = false, skipOn = Advice.OnNonDefaultValue.class)
  public static Object onEnter(
      @Name String name, @Key String keyPattern, @AllArguments Object[] args) {
    String key = CacheKeyBuilder.buildKey(keyPattern, args);
    return CacheAdviceExecutor.get(name, key);
  }

  @OnMethodExit(inline = false, onThrowable = Throwable.class)
  public static Object exit(
      @Name String name,
      @Key String keyPattern,
      @Async boolean async,
      @Enter Object enterValue,
      @Return Object returnValue,
      @Thrown Throwable thrown,
      @AllArguments Object[] args) {

    if (null != enterValue) {
      return enterValue;
    }

    String key = CacheKeyBuilder.buildKey(keyPattern, args);

    if (null == thrown) {
      CacheAdviceExecutor.put(name, key, async, returnValue);
    }

    return returnValue;
  }
}
