/* Licensed under Apache-2.0 2025. */
package test;

import github.benslabbert.vdw.codegen.annotation.AroundAdvice;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@AroundAdvice(Observed.Impl.class)
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
