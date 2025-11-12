/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.AroundAdvice.AroundAdviceInvocation;
import github.benslabbert.vdw.codegen.annotation.BeforeAdvice.BeforeAdviceInvocation;
import jakarta.inject.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class AdviceExecutor {

  private AdviceExecutor() {}

  private static final Map<String, List<Provider<? extends BeforeAdviceInvocation>>> MAP =
      new ConcurrentHashMap<>();

  private static final Comparator<BeforeAdviceInvocation> COMPARATOR =
      Comparator.comparingInt(BeforeAdviceInvocation::priority);

  public static void clear() {
    MAP.clear();
  }

  public static <T extends BeforeAdviceInvocation> void addAdvice(
      String adviceName, Provider<T> provider) {
    MAP.compute(
        adviceName,
        (key, oldValue) -> {
          if (oldValue == null) {
            var providers = new ArrayList<Provider<? extends BeforeAdviceInvocation>>(2);
            providers.add(provider);
            return providers;
          }
          oldValue.add(provider);
          return oldValue;
        });
  }

  public static void before(String advices, String clazz, String method, Object... args) {
    for (var bi : getAdvicesHighestPriority(advices)) {
      bi.before(clazz, method, args);
    }
  }

  public static void after(String advices, String clazz, String method, Object returnValue) {
    for (var bi : getAdvicesLowestPriority(advices)) {
      if (bi instanceof AroundAdviceInvocation ai) {
        ai.after(clazz, method, returnValue);
      }
    }
  }

  public static void after(String advices, String clazz, String method) {
    for (var bi : getAdvicesLowestPriority(advices)) {
      if (bi instanceof AroundAdviceInvocation ai) {
        ai.after(clazz, method);
      }
    }
  }

  public static Throwable exceptionally(String advices, String clazz, String method, Throwable t) {
    try {
      for (var bi : getAdvicesLowestPriority(advices)) {
        if (bi instanceof AroundAdviceInvocation ai) {
          t = ai.exceptionally(clazz, method, t);
        }
      }
      return t;
    } catch (Throwable newT) {
      newT.addSuppressed(t);
      return newT;
    }
  }

  private static Iterable<BeforeAdviceInvocation> getAdvicesHighestPriority(String advices) {
    return getAdvices(advices, COMPARATOR.reversed());
  }

  private static Iterable<BeforeAdviceInvocation> getAdvicesLowestPriority(String advices) {
    return getAdvices(advices, COMPARATOR);
  }

  private static Iterable<BeforeAdviceInvocation> getAdvices(
      String advices, Comparator<BeforeAdviceInvocation> comparator) {
    Stream<BeforeAdviceInvocation> stream =
        Arrays.stream(advices.split(","))
            .map(s -> MAP.getOrDefault(s, List.of()))
            .flatMap(Collection::stream)
            .map(Provider::get)
            .map(s -> (BeforeAdviceInvocation) s)
            .sorted(comparator);

    return stream::iterator;
  }
}
