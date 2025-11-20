/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@interface CacheData {

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @interface Name {}

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @interface Key {}

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @interface Policy {}

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @interface Async {}
}
