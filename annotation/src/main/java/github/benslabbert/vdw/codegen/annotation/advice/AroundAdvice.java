/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation.advice;

import jakarta.annotation.Nullable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// When AroundAdvice is executed the highest priority advices will be executed first
/// however, on the after and exception calls, this is reversed.
///
/// Given advices A1, A2 and A3 where A1.priority > A2.priority > A3.priority
///
/// The advices will be executed as follows:
///
/// before: A1 -> A2 -> A3
/// after: A3 -> A2 -> A1
/// exceptionally: A3 -> A2 -> A1
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AroundAdvice {

  Class<? extends AroundAdviceInvocation> value();

  interface AroundAdviceInvocation extends BeforeAdvice.BeforeAdviceInvocation {

    void after(String className, String methodName, Object returnValue);

    void after(String className, String methodName);

    /// If the advised method completes exceptionally you can handle the exception here.
    ///
    /// @return the original or modified exception for further processing or null if the exception
    /// can be handled. **NOTE** if this advices handles the exception (i.e. returns a {@code null})
    /// this value will be passed to further advices
    default Throwable exceptionally(String className, String methodName, @Nullable Throwable t) {
      return t;
    }
  }
}
