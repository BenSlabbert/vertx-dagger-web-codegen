/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.GenerateModuleBindings;
import github.benslabbert.vdw.codegen.annotation.RequiresModuleGeneration;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;

public class ModuleGeneration extends ProcessorBase {

  public ModuleGeneration() {
    super(Set.of(RequiresModuleGeneration.class.getCanonicalName()));
  }

  @Override
  List<GeneratedFile> generateTempFile(Element element) {
    printNote("generate dagger module", element);

    String canonicalName = element.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    Name handlerName = element.getSimpleName();
    String generatedRouterClassName = handlerName.toString();
    String generatedModuleClassName = handlerName + "_ModuleBindings";

    printNote(
        "generate module class %s for handler %s"
            .formatted(generatedModuleClassName, generatedRouterClassName),
        element);

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", classPackage);
      out.println();
      out.println("import jakarta.annotation.Generated;");
      out.println("import dagger.Binds;");
      out.println("import dagger.Module;");
      out.println("import dagger.multibindings.IntoSet;");
      out.println("import github.benslabbert.vdw.codegen.commons.RouterConfigurer;");
      out.printf("import %s;%n", GenerateModuleBindings.class.getCanonicalName());
      out.println();
      out.println("@GenerateModuleBindings");
      out.println("@Module");
      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.println("interface " + generatedModuleClassName + " {");
      out.println();
      out.println("\t@Binds");
      out.println("\t@IntoSet");
      out.printf("\tRouterConfigurer routerConfigurer(%s router);%n", generatedRouterClassName);
      out.println("}");
      out.println();
    }

    return List.of(new GeneratedFile(stringWriter, classPackage + "." + generatedModuleClassName));
  }
}
