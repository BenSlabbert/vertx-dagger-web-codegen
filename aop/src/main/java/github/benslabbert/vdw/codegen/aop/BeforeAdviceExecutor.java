/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.BeforeAdvice.BeforeAdviceInvocation;
import jakarta.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeforeAdviceExecutor {

  private static final Logger log = LoggerFactory.getLogger(BeforeAdviceExecutor.class);
  private static final Map<String, Provider<BeforeAdviceInvocation>> ADVICE_MAP = new HashMap<>();

  public static void addBeforeAdvice(
      String adviceCanonicalName, Provider<BeforeAdviceInvocation> beforeAdviceInvocationProvider) {
    Provider<BeforeAdviceInvocation> put =
        ADVICE_MAP.put(adviceCanonicalName, beforeAdviceInvocationProvider);
    if (put != null) {
      throw new IllegalStateException("Duplicate before advice");
    }
  }

  public static void before(
      String adviceCanonicalName, String className, String methodName, Object... args) {
    Provider<BeforeAdviceInvocation> invocationProvider = ADVICE_MAP.get(adviceCanonicalName);

    if (null == invocationProvider) {
      log.warn("No before advice found for {}", className);
      return;
    }

    BeforeAdviceInvocation beforeAdviceInvocation = invocationProvider.get();
    beforeAdviceInvocation.before(className, methodName, args);
  }
}
