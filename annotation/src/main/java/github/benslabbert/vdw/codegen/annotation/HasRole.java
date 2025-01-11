/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface HasRole {

  // TODO: this should be allowed on the class level as well and applied to all handlers
  String value();
}
