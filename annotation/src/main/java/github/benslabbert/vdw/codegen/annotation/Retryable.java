/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Retryable {

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  @interface FixedDelay {}

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  @interface ExponentialBackoff {}
}
