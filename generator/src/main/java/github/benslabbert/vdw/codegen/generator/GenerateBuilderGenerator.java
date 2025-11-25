/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.WildcardTypeName;
import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class GenerateBuilderGenerator extends ProcessorBase {

  public GenerateBuilderGenerator() {
    super(Set.of(GenerateBuilder.class.getCanonicalName()));
  }

  @Override
  List<GeneratedFile> generateTempFile(Element element) {
    if (!(element instanceof TypeElement typeElement)) {
      printError("@Builder can only be applied to types");
      throw new GenerationException("@Builder can only be applied to types");
    }

    if (typeElement.getKind() != ElementKind.RECORD) {
      throw new GenerationException("@Builder can only be applied to record types");
    }

    GeneratedFile generatedFile = generateBuilderForRecord(typeElement);

    return List.of(generatedFile);
  }

  private GeneratedFile generateBuilderForRecord(TypeElement recordType) {
    String modifier = "";
    boolean isPublic = recordType.getModifiers().contains(Modifier.PUBLIC);
    if (isPublic) {
      modifier = "public";
    }
    boolean isProtected = recordType.getModifiers().contains(Modifier.PROTECTED);
    if (isProtected) {
      modifier = "protected";
    }
    String recordSimpleName = recordType.getSimpleName().toString();
    String builderClassName = recordSimpleName + "Builder";

    PackageElement pkg = processingEnv.getElementUtils().getPackageOf(recordType);
    String packageName = pkg.getQualifiedName().toString();

    List<RecordComponentElement> components =
        recordType.getEnclosedElements().stream()
            .filter(e -> e.getKind() == ElementKind.RECORD_COMPONENT)
            .map(e -> (RecordComponentElement) e)
            .toList();

    String recordName = recordType.getSimpleName().toString();

    List<Result> results = components.stream().map(GenerateBuilderGenerator::map).toList();
    String componentsMethods =
        results.stream().map(s -> s.method).collect(Collectors.joining("\n\n"));

    String imports =
        results.stream()
            .flatMap(s -> s.imports.stream())
            .distinct()
            .map("import %s;"::formatted)
            .collect(Collectors.joining("\n"));

    // Name of the AutoBuilder implementation expected to be generated elsewhere
    String autoImplName = "AutoBuilder_" + builderClassName + "_Builder";

    // Text-block template for the generated source
    String template =
        """
        %s

        %s

        import com.google.auto.value.AutoBuilder;

        %s class %s {

          %s static Builder builder() {
            return new %s();
          }

          %s static Builder toBuilder(%s self) {
            return new %s(self);
          }

          @AutoBuilder(ofClass = %s.class)
          public interface Builder {
            %s
            %s build();
          }
        }
        """;

    // package line (empty string if no package)
    String packageLine = packageName.isEmpty() ? "" : "package " + packageName + ";\n";

    String src =
        String.format(
            template,
            packageLine,
            imports,
            modifier,
            builderClassName,
            modifier,
            autoImplName,
            modifier,
            recordName,
            autoImplName,
            recordName,
            // insert component methods (already indented)
            componentsMethods.isEmpty() ? "" : componentsMethods + "\n",
            // build return type
            recordName);

    // Write file
    String fullClassName =
        packageName.isEmpty() ? builderClassName : packageName + "." + builderClassName;
    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.write(src);
    }

    return new GeneratedFile(stringWriter, fullClassName);
  }

  private record Result(String method, Set<String> imports) {}

  private static Result map(RecordComponentElement rc) {
    String compName = rc.getSimpleName().toString();
    TypeMirror typeMirror = rc.asType();
    TypeMirrorFormatter.Result fmt = TypeMirrorFormatter.format(typeMirror, compName);
    return new Result(
        String.format("Builder %s(%s);", compName, fmt.simpleDeclaration), fmt.imports);
  }

  static final class TypeMirrorFormatter {

    public static class Result {
      public final String simpleDeclaration; // e.g. "Reference<Address> address"
      public final Set<String>
          imports; // e.g. "github.benslabbert.vdw.codegen.commons.jdbc.Reference",

      // "github.benslabbert.vdw.codegen.example.jdbc.Address"

      public Result(String simpleDeclaration, Set<String> imports) {
        this.simpleDeclaration = simpleDeclaration;
        this.imports = imports;
      }
    }

    public static Result format(TypeMirror typeMirror, String varName) {
      TypeName typeName = TypeName.get(typeMirror);
      Set<String> imports = new HashSet<>();
      collectClassNames(typeName, imports);
      String simple = typeName.toString() + " " + varName;

      for (String anImport : imports) {
        String[] split = anImport.split("\\.");
        var s = split[split.length - 1];
        simple = simple.replace(anImport, s);
      }

      return new Result(simple, imports);
    }

    private static void collectClassNames(TypeName tn, Set<String> out) {
      if (tn instanceof ClassName cn) {
        String pkg = cn.packageName();
        List<String> simpleNames = cn.simpleNames();
        String fq =
            pkg.isEmpty()
                ? String.join(".", simpleNames)
                : pkg + "." + String.join(".", simpleNames);
        out.add(fq);
        return;
      }
      if (tn instanceof ParameterizedTypeName) {
        ParameterizedTypeName p = (ParameterizedTypeName) tn;
        collectClassNames(p.rawType(), out);
        for (TypeName arg : p.typeArguments()) {
          collectClassNames(arg, out);
        }
        return;
      }
      if (tn instanceof ArrayTypeName) {
        collectClassNames(((ArrayTypeName) tn).componentType(), out);
        return;
      }
      if (tn instanceof WildcardTypeName w) {
        if (w.upperBounds() != null) {
          for (TypeName b : w.upperBounds()) collectClassNames(b, out);
        }
        if (w.lowerBounds() != null) {
          for (TypeName b : w.lowerBounds()) collectClassNames(b, out);
        }
        return;
      }
      // TypeVariableName and others: nothing to collect
    }
  }
}
