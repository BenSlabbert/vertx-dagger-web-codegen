/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop;

import github.benslabbert.vdw.codegen.annotation.BeforeAdvice;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@BeforeAdvice(CustomAdviceImpl.class)
@interface CustomAdvice {}
