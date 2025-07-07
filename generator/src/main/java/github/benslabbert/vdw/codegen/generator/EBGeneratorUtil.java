/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import github.benslabbert.vdw.codegen.annotation.HasRole;
import io.vertx.core.Future;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class EBGeneratorUtil {

  private EBGeneratorUtil() {}

  static List<ServiceMethod> getMethods(Element e) {
    return e.getEnclosedElements().stream()
        .filter(f -> f.getKind() == ElementKind.METHOD)
        .map(el -> (ExecutableElement) el)
        .filter(
            f -> {
              TypeKind returnTypeKind = f.getReturnType().getKind();
              if (TypeKind.DECLARED != returnTypeKind) {
                throw new GenerationException(
                    "cannot have void return types, only io.vertx.core.Future");
              }
              return true;
            })
        .map(
            sm -> {
              ParameterizedTypeName typeName =
                  (ParameterizedTypeName) TypeName.get(sm.getReturnType());
              ClassName rawType = typeName.rawType();
              if (!rawType.canonicalName().equals(Future.class.getCanonicalName())) {
                throw new GenerationException("must return: " + Future.class.getCanonicalName());
              }
              List<TypeName> typeArguments = typeName.typeArguments();

              TypeName returnType = typeArguments.getFirst();

              List<? extends VariableElement> parameters = sm.getParameters();
              if (1 != parameters.size()) {
                throw new GenerationException("method can only have one parameter");
              }
              VariableElement parameter = parameters.getFirst();
              TypeMirror typeMirror = parameter.asType();
              TypeName tn = TypeName.get(typeMirror);
              String parameterTypeCanonicalName = tn.withoutAnnotations().toString();

              HasRole hasRoles = sm.getAnnotation(HasRole.class);
              List<String> roles = null;
              if (null != hasRoles) {
                roles = Arrays.stream(hasRoles.value()).distinct().toList();
              }

              String paramSimpleName =
                  parameterTypeCanonicalName.substring(
                      parameterTypeCanonicalName.lastIndexOf('.') + 1);
              return new ServiceMethod(
                  returnType.toString(),
                  returnType.toString().substring(returnType.toString().lastIndexOf('.') + 1),
                  parameterTypeCanonicalName,
                  paramSimpleName,
                  sm.getSimpleName().toString(),
                  roles,
                  null != parameter.getAnnotation(Valid.class));
            })
        .toList();
  }

  record ServiceMethod(
      String returnTypeImport,
      String returnTypeName,
      String paramTypeImport,
      String paramTypeName,
      String methodName,
      @Nullable List<String> roles,
      boolean validated) {}
}
