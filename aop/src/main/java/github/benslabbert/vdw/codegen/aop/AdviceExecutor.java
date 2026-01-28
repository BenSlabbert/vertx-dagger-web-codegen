/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.advice.AroundAdvice.AroundAdviceInvocation;
import github.benslabbert.vdw.codegen.annotation.advice.BeforeAdvice.BeforeAdviceInvocation;
import jakarta.inject.Provider;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.CRC32;

public final class AdviceExecutor {

  private AdviceExecutor() {}

  private static final Map<Long, List<Provider<? extends BeforeAdviceInvocation>>> MAP =
      new ConcurrentHashMap<>();

  private static final Comparator<BeforeAdviceInvocation> COMPARATOR =
      Comparator.comparingInt(BeforeAdviceInvocation::priority);

  public static void clear() {
    MAP.clear();
  }

  public static <T extends BeforeAdviceInvocation> void addAdvice(
      String adviceName, Provider<T> provider) {
    CRC32 crc32 = new CRC32();
    crc32.update(adviceName.getBytes(StandardCharsets.UTF_8));
    long value = crc32.getValue();
    MAP.compute(
        value,
        (_, oldValue) -> {
          if (oldValue == null) {
            List<Provider<? extends BeforeAdviceInvocation>> providers = new ArrayList<>(2);
            providers.add(provider);
            return providers;
          }
          oldValue.add(provider);
          return oldValue;
        });
  }

  public static void before(long mask, String clazz, String method, Object... args) {
    for (var bi : getAdvicesHighestPriority(mask)) {
      bi.before(clazz, method, args);
    }
  }

  public static void after(long mask, String clazz, String method, Object returnValue) {
    for (var bi : getAdvicesLowestPriority(mask)) {
      if (bi instanceof AroundAdviceInvocation ai) {
        ai.after(clazz, method, returnValue);
      }
    }
  }

  public static void after(long mask, String clazz, String method) {
    for (var bi : getAdvicesLowestPriority(mask)) {
      if (bi instanceof AroundAdviceInvocation ai) {
        ai.after(clazz, method);
      }
    }
  }

  public static Throwable exceptionally(long mask, String clazz, String method, Throwable t) {
    try {
      for (var bi : getAdvicesLowestPriority(mask)) {
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

  private static Iterable<BeforeAdviceInvocation> getAdvicesHighestPriority(long mask) {
    return getAdvices(mask, COMPARATOR.reversed());
  }

  private static Iterable<BeforeAdviceInvocation> getAdvicesLowestPriority(long mask) {
    return getAdvices(mask, COMPARATOR);
  }

  private static Iterable<BeforeAdviceInvocation> getAdvices(
      long mask, Comparator<BeforeAdviceInvocation> comparator) {

    Stream<BeforeAdviceInvocation> stream =
        MAP.entrySet().stream()
            .filter(
                e -> {
                  Long key = e.getKey();
                  return (mask & key) == key;
                })
            .map(Map.Entry::getValue)
            .flatMap(Collection::stream)
            .map(Provider::get)
            .map(s -> (BeforeAdviceInvocation) s)
            .sorted(comparator);

    return stream::iterator;
  }
}
