/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.BeforeAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEntryAdvice implements BeforeAdvice.BeforeAdviceInvocation {

  private static final Logger log = LoggerFactory.getLogger(LogEntryAdvice.class);

  public LogEntryAdvice() {}

  @Override
  public void before(String className, String methodName, Object... args) {
    log.info("LogEntryAdvice.before({}, {})", className, methodName);
  }
}
