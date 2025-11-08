/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.BeforeAdvice.BeforeAdviceInvocation;
import jakarta.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeforeAdviceExecutor {

  private static final Map<String, List<Provider<BeforeAdviceInvocation>>> MAP = new HashMap<>();

  public static void clear() {
    MAP.clear();
  }

  public static void addBeforeAdvice(String adviceName, Provider<BeforeAdviceInvocation> provider) {
    MAP.compute(
        adviceName,
        (key, oldValue) -> {
          if (null == oldValue) {
            return new ArrayList<>(2);
          }

          oldValue.add(provider);
          return oldValue;
        });
  }

  public static void before(String adviceName, String clazz, String method, Object... args) {
    MAP.getOrDefault(adviceName, List.of()).stream()
        .map(Provider::get)
        .forEach(i -> i.before(clazz, method, args));
  }
}
