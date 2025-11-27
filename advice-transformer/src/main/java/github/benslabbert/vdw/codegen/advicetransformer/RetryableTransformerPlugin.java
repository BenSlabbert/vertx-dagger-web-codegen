/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.annotation.advice.Retryable.ExponentialBackoff;
import github.benslabbert.vdw.codegen.annotation.advice.Retryable.FixedDelay;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.build.BuildLogger;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;

public class RetryableTransformerPlugin implements Plugin {
  private final BuildLogger log;

  public RetryableTransformerPlugin(BuildLogger logger) {
    this.log = logger;
  }

  @Override
  public DynamicType.Builder<?> apply(
      DynamicType.Builder<?> builder, TypeDescription target, ClassFileLocator cfl) {

    List<InDefinedShape> fixed = new ArrayList<>();
    List<InDefinedShape> exponential = new ArrayList<>();

    target.getDeclaredMethods().stream()
        .filter(this::shapeMatches)
        .forEach(
            shape -> {
              if (shape.getDeclaredAnnotations().isAnnotationPresent(ExponentialBackoff.class)) {
                exponential.add(shape);
              } else {
                fixed.add(shape);
              }
            });

    for (InDefinedShape shape : exponential) {
      builder =
          builder
              .method(md -> md.equals(shape))
              .intercept(MethodDelegation.to(ExponentialBackoffRetryableAdvice.class))
              .annotateMethod(AlreadyTransformedUtil.alreadyTransformed());
    }

    for (InDefinedShape shape : fixed) {
      builder =
          builder
              .method(md -> md.equals(shape))
              .intercept(MethodDelegation.to(FixedRetryableAdvice.class))
              .annotateMethod(AlreadyTransformedUtil.alreadyTransformed());
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
    return annotations.isAnnotationPresent(ExponentialBackoff.class)
        || annotations.isAnnotationPresent(FixedDelay.class);
  }
}
