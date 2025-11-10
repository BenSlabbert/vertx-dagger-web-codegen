/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.AroundAdvice.AroundAdviceInvocation;
import github.benslabbert.vdw.codegen.annotation.BeforeAdvice.BeforeAdviceInvocation;
import jakarta.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdviceExecutor {

  private static final Map<String, List<Provider<? extends BeforeAdviceInvocation>>> MAP =
      new HashMap<>();

  public static void clear() {
    MAP.clear();
  }

  private AdviceExecutor() {}

  public static void addBeforeAdvice(String adviceName, Provider<BeforeAdviceInvocation> provider) {
    MAP.compute(
        adviceName,
        (key, oldValue) -> {
          if (null == oldValue) {
            var providers = new ArrayList<Provider<? extends BeforeAdviceInvocation>>(2);
            providers.add(provider);
            return providers;
          }

          oldValue.add(provider);
          return oldValue;
        });
  }

  public static void addAroundAdvice(String adviceName, Provider<AroundAdviceInvocation> provider) {
    MAP.compute(
        adviceName,
        (key, oldValue) -> {
          if (null == oldValue) {
            var providers = new ArrayList<Provider<? extends BeforeAdviceInvocation>>(2);
            providers.add(provider);
            return providers;
          }

          oldValue.add(provider);
          return oldValue;
        });
  }

  public static void before(String advices, String clazz, String method, Object... args) {
    for (String s : advices.split(",")) {
      for (Provider<? extends BeforeAdviceInvocation> p : MAP.getOrDefault(s, List.of())) {
        BeforeAdviceInvocation bi = p.get();
        bi.before(clazz, method, args);
      }
    }
  }

  public static void after(String advices, String clazz, String method, Object returnValue) {
    for (String s : advices.split(",")) {
      for (Provider<? extends BeforeAdviceInvocation> p : MAP.getOrDefault(s, List.of())) {
        BeforeAdviceInvocation bi = p.get();
        if (bi instanceof AroundAdviceInvocation ai) {
          ai.after(clazz, method, returnValue);
        }
      }
    }
  }

  public static void after(String advices, String clazz, String method) {
    for (String s : advices.split(",")) {
      for (Provider<? extends BeforeAdviceInvocation> p : MAP.getOrDefault(s, List.of())) {
        BeforeAdviceInvocation bi = p.get();
        if (bi instanceof AroundAdviceInvocation ai) {
          ai.after(clazz, method);
        }
      }
    }
  }

  public static Throwable exceptionally(String advices, String clazz, String method, Throwable t) {
    for (String s : advices.split(",")) {
      for (Provider<? extends BeforeAdviceInvocation> p : MAP.getOrDefault(s, List.of())) {
        BeforeAdviceInvocation bi = p.get();
        if (bi instanceof AroundAdviceInvocation ai) {
          t = ai.exceptionally(clazz, method, t);
        }
      }
    }
    return t;
  }
}
