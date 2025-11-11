/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;

import github.benslabbert.vdw.codegen.aop.AdviceExecutor;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.asm.Advice.Thrown;

public final class ApplyAdvice {

  private ApplyAdvice() {}

  @OnMethodEnter(inline = false)
  public static void onEnter(
      @Origin("#t") String className,
      @Origin("#m") String methodName,
      @AdviceName String adviceNames,
      @AllArguments Object[] args) {
    AdviceExecutor.before(adviceNames, className, methodName, args);
  }

  @OnMethodExit(onThrowable = Throwable.class)
  public static void exit(
      @Origin("#t") String className,
      @Origin("#m") String methodName,
      @AdviceName String adviceNames,
      @Return(typing = DYNAMIC) Object returnValue,
      @Thrown(readOnly = false) Throwable thrown) {
    if (null != thrown) {
      // if throwable is not null, it will be thrown later
      thrown = AdviceExecutor.exceptionally(adviceNames, className, methodName, thrown);
      return;
    }

    if (null == returnValue) {
      AdviceExecutor.after(adviceNames, className, methodName);
    } else {
      AdviceExecutor.after(adviceNames, className, methodName, returnValue);
    }
  }
}
