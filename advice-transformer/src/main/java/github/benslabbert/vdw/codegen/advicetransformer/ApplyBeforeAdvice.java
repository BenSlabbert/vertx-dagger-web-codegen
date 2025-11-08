/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.aop.BeforeAdviceExecutor;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.Origin;

final class ApplyBeforeAdvice {

  private ApplyBeforeAdvice() {}

  @OnMethodEnter
  static void onEnter(
      @Origin("#t") String className,
      @Origin("#m") String methodName,
      @AdviceName String adviceName,
      @AllArguments Object[] args) {
    BeforeAdviceExecutor.before(adviceName, className, methodName, args);
  }
}
