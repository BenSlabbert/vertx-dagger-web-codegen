/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator.advice;

import github.benslabbert.vdw.codegen.annotation.advice.AroundAdvice;
import github.benslabbert.vdw.codegen.annotation.advice.BeforeAdvice;
import github.benslabbert.vdw.codegen.commons.hash.Murmur3;
import github.benslabbert.vdw.codegen.generator.GenerationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public class AdviceResourceGenerator extends AbstractProcessor {

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
    return Set.of(BeforeAdvice.class.getCanonicalName(), AroundAdvice.class.getCanonicalName());
  }

  private record Advice(
      String adviceAnnotation, String adviceImplementation, String type, long hash) {}

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) {
      return false;
    }

    List<Advice> advices =
        annotations.stream()
            .flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
            .map(this::getAdvice)
            .toList();

    if (advices.isEmpty()) {
      return false;
    }

    writeResourceFile(advices);

    return true;
  }

  private void writeResourceFile(List<Advice> advices) {
    try {
      FileObject adviceResource =
          processingEnv
              .getFiler()
              .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/advice_annotations");

      try (var w = new PrintWriter(adviceResource.openWriter())) {
        advices.forEach(
            a ->
                w.printf(
                    "%s,%s,%s,%d%n", a.adviceAnnotation, a.adviceImplementation, a.type, a.hash));
      }
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private Advice getAdvice(Element e) {
    BeforeAdvice beforeAdvice = e.getAnnotation(BeforeAdvice.class);
    AroundAdvice aroundAdvice = e.getAnnotation(AroundAdvice.class);

    if (null == beforeAdvice && null == aroundAdvice) {
      throw new GenerationException("both cannot be null");
    }

    String type;
    ThrowsMirroredTypeException t;
    if (null != beforeAdvice) {
      t = beforeAdvice::value;
      type = "before";
    } else {
      t = aroundAdvice::value;
      type = "around";
    }

    String adviceImplementation = getReturnType(t);
    String adviceAnnotation = e.asType().toString();
    long hash = Murmur3.hash(adviceAnnotation);
    return new Advice(adviceAnnotation, adviceImplementation, type, hash);
  }

  private String getReturnType(ThrowsMirroredTypeException callable) {
    try {
      Object ignore = callable.call();
      throw new GenerationException("expected MirroredTypeException");
    } catch (MirroredTypeException e) {
      TypeMirror typeMirror = e.getTypeMirror();
      DeclaredType declaredType = (DeclaredType) typeMirror;
      TypeElement typeElement = (TypeElement) declaredType.asElement();
      return typeElement.getQualifiedName().toString();
    }
  }

  private interface ThrowsMirroredTypeException {

    Object call() throws MirroredTypeException;
  }
}
