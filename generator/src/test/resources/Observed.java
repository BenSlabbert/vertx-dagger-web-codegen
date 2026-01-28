/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.advice.AroundAdvice;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@AroundAdvice(value = Observed.Impl.class, id = 1)
public @interface Observed {

  class Impl implements AroundAdvice.AroundAdviceInvocation {

    @Override
    public void before(String className, String methodName, Object... args) {}

    @Override
    public void after(String className, String methodName, Object returnValue) {}

    @Override
    public void after(String className, String methodName) {}
  }
}
