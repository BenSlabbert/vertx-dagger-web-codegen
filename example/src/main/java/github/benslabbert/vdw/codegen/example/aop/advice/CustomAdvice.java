/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop.advice;

import github.benslabbert.vdw.codegen.annotation.advice.BeforeAdvice;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@BeforeAdvice(value = CustomAdviceImpl.class, id = 300)
@interface CustomAdvice {

  int priority() default 1;
}
