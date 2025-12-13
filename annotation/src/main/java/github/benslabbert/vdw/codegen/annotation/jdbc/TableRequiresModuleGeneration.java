/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation.jdbc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Used by the annotation processor to generate dagger bindings for generated JDBC repositories. */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface TableRequiresModuleGeneration {
  /** The interface that the annotated implementation class implements. */
  Class<?> value();
}
