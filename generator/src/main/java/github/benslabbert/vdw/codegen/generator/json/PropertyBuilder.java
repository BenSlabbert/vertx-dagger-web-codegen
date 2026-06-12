/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator.json;

import com.palantir.javapoet.TypeName;
import github.benslabbert.vdw.codegen.generator.GenerationException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

class PropertyBuilder {

  private static final String NOT_NULL = "jakarta.validation.constraints.NotNull";
  private static final String NOT_BLANK = "jakarta.validation.constraints.NotBlank";
  private static final String NOT_EMPTY = "jakarta.validation.constraints.NotEmpty";
  private static final String MIN = "jakarta.validation.constraints.Min";
  private static final String MAX = "jakarta.validation.constraints.Max";
  private static final String SIZE = "jakarta.validation.constraints.Size";

  private PropertyBuilder() {}

  static List<Property> getProperties(Element e, Elements elements) {
    List<Property> properties = new ArrayList<>();

    if (!(e instanceof TypeElement)) {
      throw new GenerationException(
          String.format(
              "Expected TypeElement for property extraction, but received: %s (kind: %s, element:"
                  + " %s)",
              e.getClass().getSimpleName(), e.getKind(), e));
    }

    TypeElement typeElement = (TypeElement) e;
    ElementKind elementKind = typeElement.getKind();

    switch (elementKind) {
      case RECORD -> {
        // For records, extract non-static fields.
        // RecordComponentElement does not work here as the annotations do not have
        // @Target(ElementType.RECORD_COMPONENT) as their target
        List<? extends Element> recordComponents =
            e.getEnclosedElements().stream()
                .filter(f -> f.getKind() == ElementKind.FIELD)
                .filter(f -> !f.getModifiers().contains(Modifier.STATIC))
                .toList();

        for (Element enclosedElement : recordComponents) {
          properties.add(extractPropertyFromField((VariableElement) enclosedElement));
        }
      }
      case INTERFACE -> {
        // For interfaces, extract all (including inherited) non-static, non-private,
        // no-parameter methods with a non-void return type.
        Map<String, ExecutableElement> methods = new LinkedHashMap<>();
        for (Element enclosedElement : elements.getAllMembers(typeElement)) {
          if (enclosedElement.getKind() != ElementKind.METHOD) {
            continue;
          }

          ExecutableElement method = (ExecutableElement) enclosedElement;
          if (method.getEnclosingElement().getKind() != ElementKind.INTERFACE
              || method.getModifiers().contains(Modifier.STATIC)
              || method.getModifiers().contains(Modifier.PRIVATE)
              || !method.getParameters().isEmpty()
              || method.getReturnType().getKind() == TypeKind.VOID) {
            continue;
          }

          methods.putIfAbsent(method.getSimpleName().toString(), method);
        }

        for (ExecutableElement method : methods.values()) {
          properties.add(extractPropertyFromMethod(method));
        }

        if (properties.isEmpty()) {
          throw new GenerationException(
              String.format(
                  "Interface %s does not declare any eligible getter-like methods with no"
                      + " parameters",
                  typeElement.getQualifiedName()));
        }
      }
      default ->
          throw new GenerationException(
              String.format(
                  "Expected RECORD or INTERFACE but received: %s (kind: %s, element: %s)",
                  e.getClass().getSimpleName(), elementKind, e));
    }

    return properties;
  }

  private static Property extractPropertyFromField(VariableElement re) {
    // name of the variable
    Name varName = re.getSimpleName();
    // type of the variable
    TypeMirror type = re.asType();
    // TypeKind.DECLARED -> this is an object
    TypeKind kind = type.getKind();
    Min min = re.getAnnotation(Min.class);
    Max max = re.getAnnotation(Max.class);
    Size size = re.getAnnotation(Size.class);
    NotEmpty notEmpty = re.getAnnotation(NotEmpty.class);

    // if type is declared and java.lang.String it is ok
    if (TypeKind.DECLARED == kind) {
      Property property = fromPreparedType(type, re, varName, kind, min, max, size, notEmpty);
      return property;
    } else if (kind.isPrimitive()) {
      Property property =
          new Property(
              varName.toString(),
              false,
              false,
              null,
              kind,
              false,
              false,
              min,
              max,
              size,
              List.of());
      return property;
    } else {
      String msg = String.format("unsupported kind: %s", kind);
      throw new GenerationException(msg);
    }
  }

  private static Property extractPropertyFromMethod(ExecutableElement method) {
    // name of the method (e.g., "name" from "name()")
    Name methodName = method.getSimpleName();
    // return type of the method
    TypeMirror type = method.getReturnType();
    // TypeKind.DECLARED -> this is an object
    TypeKind kind = type.getKind();
    Min min = method.getAnnotation(Min.class);
    Max max = method.getAnnotation(Max.class);
    Size size = method.getAnnotation(Size.class);
    NotEmpty notEmpty = method.getAnnotation(NotEmpty.class);

    // if type is declared and java.lang.String it is ok
    if (TypeKind.DECLARED == kind) {
      return fromPreparedType(type, method, methodName, kind, min, max, size, notEmpty);
    } else if (kind.isPrimitive()) {
      return new Property(
          methodName.toString(), false, false, null, kind, false, false, min, max, size, List.of());
    } else {
      String msg = String.format("unsupported kind: %s", kind);
      throw new GenerationException(msg);
    }
  }

  private static Property fromPreparedType(
      TypeMirror type,
      Element element,
      Name varName,
      TypeKind kind,
      Min min,
      Max max,
      Size size,
      NotEmpty notEmpty) {
    boolean nullable = null != element.getAnnotation(Nullable.class);
    boolean notBlank = null != element.getAnnotation(NotBlank.class);

    List<GenericParameterAnnotation> genericParameterAnnotations =
        getGenericParameterAnnotations((DeclaredType) type);

    TypeName tn = TypeName.get(type);
    TypeName typeNameWithoutAnnotations = tn.withoutAnnotations();
    String typeString = typeNameWithoutAnnotations.toString();
    return new Property(
        varName.toString(),
        nullable,
        true,
        typeString,
        kind,
        notBlank,
        null != notEmpty,
        min,
        max,
        size,
        genericParameterAnnotations);
  }

  private static List<GenericParameterAnnotation> getGenericParameterAnnotations(
      DeclaredType declaredType) {
    List<? extends TypeMirror> ta = declaredType.getTypeArguments();
    if (ta.isEmpty()) {
      return List.of();
    }
    if (ta.size() > 1) {
      throw new GenerationException("only support generic types with single generic type argument");
    }

    TypeMirror genericParameter = ta.getFirst();
    List<GenericParameterAnnotation> arr = new ArrayList<>();

    for (AnnotationMirror am : genericParameter.getAnnotationMirrors()) {
      DeclaredType annotationType = am.getAnnotationType();
      Element element = annotationType.asElement();
      String annotationClassName = element.asType().toString();
      switch (annotationClassName) {
        case NOT_NULL -> {
          GenericParameterAnnotation a = GenericParameterAnnotation.NotNull.create();
          arr.add(a);
        }
        case NOT_BLANK -> {
          GenericParameterAnnotation a = GenericParameterAnnotation.NotBlank.create();
          arr.add(a);
        }
        case NOT_EMPTY -> {
          GenericParameterAnnotation a = GenericParameterAnnotation.NotEmpty.create();
          arr.add(a);
        }
        case MIN -> {
          GenericParameterAnnotation a = GenericParameterAnnotation.Min.create(am);
          arr.add(a);
        }
        case MAX -> {
          GenericParameterAnnotation a = GenericParameterAnnotation.Max.create(am);
          arr.add(a);
        }
        case SIZE -> {
          GenericParameterAnnotation a = GenericParameterAnnotation.Size.create(am);
          arr.add(a);
        }
        case null, default ->
            throw new GenerationException("unsupported annotation: " + annotationClassName);
      }
    }

    return List.copyOf(arr);
  }
}
