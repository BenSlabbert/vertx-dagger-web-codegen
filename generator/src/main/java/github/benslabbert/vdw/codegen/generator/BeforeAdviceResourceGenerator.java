/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.BeforeAdvice;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public class BeforeAdviceResourceGenerator extends AbstractProcessor {

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedOptions() {
    return Set.of();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(BeforeAdvice.class.getCanonicalName());
  }

  private record Advice(String adviceImplementation, String adviceAnnotation) {}

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      List<Advice> advices =
          roundEnv.getElementsAnnotatedWith(annotation).stream()
              .map(
                  e -> {
                    // todo: ensure the implementation implements
                    //
                    // github.benslabbert.vdw.codegen.annotation.BeforeAdvice.BeforeAdviceInvocation
                    BeforeAdvice beforeAdvice = e.getAnnotation(BeforeAdvice.class);
                    String adviceImplementation = getReturnType(beforeAdvice);
                    String adviceAnnotation = e.asType().toString();
                    return new Advice(adviceImplementation, adviceAnnotation);
                  })
              .toList();

      try {
        FileObject adviceResource =
            processingEnv
                .getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/advice_annotations");

        try (var w = new PrintWriter(adviceResource.openWriter())) {
          advices.forEach(a -> w.println(a.adviceAnnotation + "=" + a.adviceImplementation));
        }
      } catch (IOException e) {
        throw new GenerationException(e);
      }

      return true;
    }

    return false;
  }

  private static String getReturnType(BeforeAdvice beforeAdvice) {
    try {
      var ignore = beforeAdvice.value(); // NOSONAR this method invocation thrown
      throw new GenerationException("expected MirroredTypeException");
    } catch (MirroredTypeException e) {
      TypeMirror typeMirror = e.getTypeMirror();
      DeclaredType declaredType = (DeclaredType) typeMirror;
      TypeElement typeElement = (TypeElement) declaredType.asElement();
      return typeElement.getQualifiedName().toString();
    }
  }
}
