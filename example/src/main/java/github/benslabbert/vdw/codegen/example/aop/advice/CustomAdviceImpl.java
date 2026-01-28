/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop.advice;

import github.benslabbert.vdw.codegen.annotation.advice.BeforeAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomAdviceImpl implements BeforeAdvice.BeforeAdviceInvocation {

  private static final Logger log = LoggerFactory.getLogger(CustomAdviceImpl.class);

  public CustomAdviceImpl() {}

  @Override
  public void before(String className, String methodName, Object... args) {
    log.info("CustomAdviceImpl.before({}, {}, {})", className, methodName, args);
  }

    @Override
    public int priority() {
        return 900;
    }
}
