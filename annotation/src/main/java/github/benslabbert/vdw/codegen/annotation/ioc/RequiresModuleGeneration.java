/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.annotation.ioc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** used by the annotation processor to generate dagger bindings for generated files. */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RequiresModuleGeneration {}
