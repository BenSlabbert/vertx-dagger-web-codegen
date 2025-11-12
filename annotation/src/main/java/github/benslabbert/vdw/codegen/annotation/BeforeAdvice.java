/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Base Annotation for implementations of Before Advice.
///
/// Implementations **must** implement {@link BeforeAdvice.BeforeAdviceInvocation}
///
/// Custom implementations and their related annotations will be included in the
/// {@code META-INF/advice_annotations} file
///
/// Custom implementations may also optionally provide an {@code int priority()} attribute, if none
/// is provided, {@code 0} will be used.
///
/// Higher priority advices will be implemented first.
/// When there is a tie, no guarantee is made in which order the advice will be invoked.
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface BeforeAdvice {

  Class<? extends BeforeAdviceInvocation> value();

  interface BeforeAdviceInvocation {

    void before(String className, String methodName, Object... args);

    default int priority() {
      return 0;
    }
  }
}
