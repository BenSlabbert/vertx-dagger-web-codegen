/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.advice.AroundAdvice;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservedImpl implements AroundAdvice.AroundAdviceInvocation {

  private static final Logger log = LoggerFactory.getLogger(ObservedImpl.class);

  @Override
  public void before(String className, String methodName, Object... args) {
    log.info("ObservedImpl.before {} {}", className, methodName);
  }

  @Override
  public void after(String className, String methodName, Object returnValue) {
    log.info("ObservedImpl.before {} {} {}", className, methodName, returnValue);
  }

  @Override
  public void after(String className, String methodName) {
    log.info("ObservedImpl.after {} {}", className, methodName);
  }

  @Override
  public Throwable exceptionally(String className, String methodName, @Nullable Throwable t) {
    if (null == t) {
      log.error(
          "ObservedImpl.exceptionally in {} {}, but original exception has been handled",
          className,
          methodName);
      return null;
    }
    log.error("ObservedImpl.exceptionally in {} {}", className, methodName, t);
    return t;
  }

  @Override
  public int priority() {
    return 100;
  }
}
