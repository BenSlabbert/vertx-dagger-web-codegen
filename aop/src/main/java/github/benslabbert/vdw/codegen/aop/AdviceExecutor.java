/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.advice.AroundAdvice.AroundAdviceInvocation;
import github.benslabbert.vdw.codegen.annotation.advice.BeforeAdvice.BeforeAdviceInvocation;
import github.benslabbert.vdw.codegen.commons.hash.Murmur3;
import jakarta.inject.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AdviceExecutor {

  private static final Logger log = LoggerFactory.getLogger(AdviceExecutor.class);

  private AdviceExecutor() {}

  private static final Map<Long, List<Provider<? extends BeforeAdviceInvocation>>> MAP =
      new ConcurrentHashMap<>();

  private static final Comparator<BeforeAdviceInvocation> COMPARATOR =
      Comparator.comparingInt(BeforeAdviceInvocation::priority);

  public static void clear() {
    log.debug("Clearing AdviceExecutor");
    MAP.clear();
  }

  public static <T extends BeforeAdviceInvocation> void addAdvice(
      String adviceName, Provider<T> provider) {
    long hash = Murmur3.hash(adviceName);

    log.debug("Adding advice {} to AdviceExecutor with hash {}", adviceName, hash);

    MAP.compute(
        hash,
        (_, providers) -> {
          if (providers == null) {
            providers = new ArrayList<>(2);
          }
          providers.add(provider);
          log.debug("Added advice {} with number of providers {}", adviceName, providers.size());
          return providers;
        });
  }

  public static void before(long mask, String clazz, String method, Object... args) {
    for (var bi : getAdvicesHighestPriority(mask)) {
      log.atDebug()
          .setMessage("execute before advice {} on class: {}, method: {}, args: {}")
          .addArgument(bi)
          .addArgument(clazz)
          .addArgument(method)
          .addArgument(args)
          .log();
      bi.before(clazz, method, args);
    }
  }

  public static void after(long mask, String clazz, String method, Object returnValue) {
    for (var bi : getAdvicesLowestPriority(mask)) {
      if (bi instanceof AroundAdviceInvocation ai) {
        log.atDebug()
            .setMessage("execute after advice {} on class: {}, method: {}, returnValue: {}")
            .addArgument(bi)
            .addArgument(clazz)
            .addArgument(method)
            .addArgument(returnValue)
            .log();
        ai.after(clazz, method, returnValue);
      }
    }
  }

  public static void after(long mask, String clazz, String method) {
    for (var bi : getAdvicesLowestPriority(mask)) {
      if (bi instanceof AroundAdviceInvocation ai) {
        log.atDebug()
            .setMessage("execute after advice {} on class: {}, method: {}")
            .addArgument(bi)
            .addArgument(clazz)
            .addArgument(method)
            .log();
        ai.after(clazz, method);
      }
    }
  }

  public static Throwable exceptionally(long mask, String clazz, String method, Throwable t) {
    try {
      for (var bi : getAdvicesLowestPriority(mask)) {
        if (bi instanceof AroundAdviceInvocation ai) {
          log.atDebug()
              .setMessage("execute exceptionally advice {} on class: {}, method: {}, throwable {}")
              .addArgument(bi)
              .addArgument(clazz)
              .addArgument(method)
              .addArgument(t)
              .log();
          var e = ai.exceptionally(clazz, method, t);
          if (null != e) {
            t.addSuppressed(e);
          }
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
