/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// When a record is annotated with {@code Builder} a new Builder class will be generated.
///
/// This replaces the need to define an interface with @{@link com.google.auto.value.AutoBuilder}
///
/// For example:
///
/// ```java
/// @Builder
/// record ResponseDto(@Nullable String data) {}
/// ```
///
/// With result in a generated class `ResponseDtoBuilder` with annotation:
///
/// ```java
///   @AutoBuilder
///   public interface Builder {
///     Builder data(@Nullable String data);
///
///     ResponseDto build();
///   }
/// ```
///
/// and can be invoked as:
///
/// ```java
/// ResponseDto dto = ResponseDtoBuilder.builder().data("data").build();
/// ```
///
/// NOTE: this only works for `record` types
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateBuilder {}
