/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The following method should return {@link Runnable} or void which will be executed after the
 * current transaction commits.
 */
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterCommit {}
