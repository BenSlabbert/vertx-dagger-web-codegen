/* Licensed under Apache-2.0 2025. */
package test;

import github.benslabbert.vdw.codegen.annotation.BeforeAdvice;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@BeforeAdvice(LogEntry.Impl.class)
public @interface LogEntry {

  class Impl implements BeforeAdvice.BeforeAdviceInvocation {

    @Override
    public void before(String className, String methodName, Object... args) {}
  }
}
