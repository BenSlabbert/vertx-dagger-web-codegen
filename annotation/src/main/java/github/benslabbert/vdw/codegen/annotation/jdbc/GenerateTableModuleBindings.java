/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation.jdbc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation used by the annotation processor to collect all generated JDBC module bindings
 * and aggregate them into a GeneratedModuleBindings interface.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateTableModuleBindings {}
