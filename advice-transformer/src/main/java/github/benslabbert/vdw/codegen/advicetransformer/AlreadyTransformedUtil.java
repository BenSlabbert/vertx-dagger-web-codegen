/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import github.benslabbert.vdw.codegen.annotation.AlreadyTransformed;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;

final class AlreadyTransformedUtil {

  private AlreadyTransformedUtil() {}

  static AnnotationDescription alreadyTransformed() {
    return AnnotationDescription.Builder.ofType(AlreadyTransformed.class).build();
  }

  static boolean alreadyTransformedPresent(MethodDescription.InDefinedShape shape) {
    return shape.getDeclaredAnnotations().isAnnotationPresent(AlreadyTransformed.class);
  }
}
