/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.annotation.Cache;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.build.BuildLogger;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.SuperMethodCall;

public class CacheTransformerPlugin implements Plugin {

  private final BuildLogger log;

  public CacheTransformerPlugin(BuildLogger logger) {
    this.log = logger;
  }

  @Override
  public DynamicType.Builder<?> apply(
      DynamicType.Builder<?> builder, TypeDescription target, ClassFileLocator cfl) {

    Map<InDefinedShape, Cache.Put> putMethods = new HashMap<>();
    Map<InDefinedShape, Cache.Evict> revokeMethods = new HashMap<>();

    target.getDeclaredMethods().stream()
        .filter(this::shapeMatches)
        .forEach(
            shape -> {
              if (shape.getDeclaredAnnotations().isAnnotationPresent(Cache.Put.class)) {
                Cache.Put put = shape.getDeclaredAnnotations().ofType(Cache.Put.class).load();
                putMethods.put(shape, put);
              } else {
                Cache.Evict revoke =
                    shape.getDeclaredAnnotations().ofType(Cache.Evict.class).load();
                revokeMethods.put(shape, revoke);
              }
            });

    for (var entry : putMethods.entrySet()) {
      InDefinedShape shape = entry.getKey();
      Cache.Put put = entry.getValue();
      log.info("put: %s".formatted(put));
      builder =
          builder
              .method(md -> md.equals(shape))
              .intercept(SuperMethodCall.INSTANCE)
              .annotateMethod(AlreadyTransformedUtil.alreadyTransformed())
              .visit(
                  Advice.withCustomMapping()
                      .bind(CacheData.Name.class, put.value())
                      .bind(CacheData.Key.class, put.key())
                      .bind(CacheData.Async.class, put.async())
                      .to(CachePutAdvice.class)
                      .on(md -> md.equals(shape)));
    }

    for (var entry : revokeMethods.entrySet()) {
      InDefinedShape shape = entry.getKey();
      Cache.Evict evict = entry.getValue();
      log.info("evict: %s".formatted(evict));
      builder =
          builder
              .method(md -> md.equals(shape))
              .intercept(SuperMethodCall.INSTANCE)
              .annotateMethod(AlreadyTransformedUtil.alreadyTransformed())
              .visit(
                  Advice.withCustomMapping()
                      .bind(CacheData.Name.class, evict.value())
                      .bind(CacheData.Key.class, evict.key())
                      .bind(CacheData.Async.class, evict.async())
                      .bind(CacheData.Policy.class, evict.policy())
                      .to(CacheEvictAdvice.class)
                      .on(md -> md.equals(shape)));
    }

    return builder;
  }

  @Override
  public void close() {
    // nothing open
    log.info("close transformer");
  }

  @Override
  public boolean matches(TypeDescription target) {
    return target.getDeclaredMethods().stream().anyMatch(this::shapeMatches);
  }

  private boolean shapeMatches(InDefinedShape shape) {
    if (AlreadyTransformedUtil.alreadyTransformedPresent(shape)) {
      log.error("cannot transform methods that have been already transformed");
      return false;
    }

    AnnotationList annotations = shape.getDeclaredAnnotations();
    return annotations.isAnnotationPresent(Cache.Put.class)
        || annotations.isAnnotationPresent(Cache.Evict.class);
  }
}
