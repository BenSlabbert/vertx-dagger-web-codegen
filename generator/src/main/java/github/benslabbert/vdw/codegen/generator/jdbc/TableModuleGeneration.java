/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator.jdbc;

import github.benslabbert.vdw.codegen.annotation.jdbc.GenerateTableModuleBindings;
import github.benslabbert.vdw.codegen.annotation.jdbc.TableRequiresModuleGeneration;
import github.benslabbert.vdw.codegen.generator.GenerationException;
import github.benslabbert.vdw.codegen.generator.ProcessorBase;
import github.benslabbert.vdw.codegen.generator.StringWriterFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Annotation processor that generates Dagger module bindings for classes annotated with
 * {@link TableRequiresModuleGeneration}.
 *
 * <p>For each annotated repository implementation class, this processor generates a module
 * interface with a {@code @Binds} method that binds the implementation to its interface.
 */
public class TableModuleGeneration extends ProcessorBase {

  public TableModuleGeneration() {
    super(Set.of(TableRequiresModuleGeneration.class.getCanonicalName()));
  }

  @Override
  protected List<GeneratedFile> generateTempFile(Element element) {
    printNote("generate JDBC repository dagger module", element);

    String canonicalName = element.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    Name implName = element.getSimpleName();
    String implClassName = implName.toString();
    String generatedModuleClassName = implClassName + "_ModuleBindings";

    // Get the interface type from the annotation
    TableRequiresModuleGeneration annotation =
        element.getAnnotation(TableRequiresModuleGeneration.class);
    String interfaceType = getInterfaceType(annotation);
    String interfaceSimpleName = interfaceType.substring(interfaceType.lastIndexOf('.') + 1);

    printNote(
        "generate module class %s for repository %s binding to %s"
            .formatted(generatedModuleClassName, implClassName, interfaceSimpleName),
        element);

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", classPackage);
      out.println();
      out.println("import jakarta.annotation.Generated;");
      out.println("import dagger.Binds;");
      out.println("import dagger.Module;");
      out.printf("import %s;%n", GenerateTableModuleBindings.class.getCanonicalName());
      out.println();
      out.println("@GenerateTableModuleBindings");
      out.println("@Module");
      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.println("interface " + generatedModuleClassName + " {");
      out.println();
      out.println("\t@Binds");
      out.println();
      
      // Method name is decapitalized interface name
      String methodName = decapitalize(interfaceSimpleName);
      // Parameter name is decapitalized impl class name
      String paramName = decapitalize(implClassName);
      
      out.printf("\t%s %s(%s %s);%n", interfaceSimpleName, methodName, implClassName, paramName);
      out.println("}");
      out.println();
    }

    return List.of(new GeneratedFile(stringWriter, classPackage + "." + generatedModuleClassName));
  }

  /**
   * Extracts the interface type from the annotation value.
   *
   * @param annotation the annotation instance
   * @return the canonical name of the interface type
   */
  private static String getInterfaceType(TableRequiresModuleGeneration annotation) {
    try {
      // This will throw MirroredTypeException
      annotation.value();
      throw new GenerationException("Expected MirroredTypeException");
    } catch (MirroredTypeException e) {
      TypeMirror typeMirror = e.getTypeMirror();
      DeclaredType declaredType = (DeclaredType) typeMirror;
      return declaredType.toString();
    }
  }

  /**
   * Decapitalizes the first character of a string.
   *
   * @param str the string to decapitalize
   * @return the decapitalized string
   */
  private static String decapitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }
}
