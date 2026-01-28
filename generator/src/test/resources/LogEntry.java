/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.advice.BeforeAdvice;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@BeforeAdvice(value = LogEntry.Impl.class, id = 100)
public @interface LogEntry {

  class Impl implements BeforeAdvice.BeforeAdviceInvocation {

    @Override
    public void before(String className, String methodName, Object... args) {}
  }
}
