/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.aop;

import github.benslabbert.vdw.codegen.annotation.advice.BeforeAdvice;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@BeforeAdvice(LogEntryAdvice.class)
public @interface LogEntry {}
