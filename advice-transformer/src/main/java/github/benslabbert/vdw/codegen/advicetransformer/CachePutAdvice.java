/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;

import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Async;
import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Key;
import github.benslabbert.vdw.codegen.advicetransformer.CacheData.Name;
import github.benslabbert.vdw.codegen.aop.cache.CacheAdviceExecutor;
import github.benslabbert.vdw.codegen.aop.cache.CacheKeyBuilder;
import java.util.Optional;
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
    Optional<String> key = CacheKeyBuilder.buildKey(keyPattern, args);
    return key.map(s -> CacheAdviceExecutor.get(name, s)).orElse(null);
  }

  @OnMethodExit(onThrowable = Throwable.class)
  public static void exit(
      @Name String name,
      @Key String keyPattern,
      @Async boolean async,
      @Enter Object enterValue,
      @Return(readOnly = false, typing = DYNAMIC) Object returnValue,
      @Thrown(readOnly = false, typing = DYNAMIC) Throwable thrown,
      @AllArguments Object[] args) {

    if (null != enterValue) {
      // override the method return value with the cached value
      returnValue = enterValue;
      // suppress any exception so the cached value is returned
      thrown = null;
      return;
    }

    Optional<String> key = CacheKeyBuilder.buildKey(keyPattern, args);

    if (null == thrown && key.isPresent()) {
      CacheAdviceExecutor.put(name, key.get(), async, returnValue);
    }
  }
}
