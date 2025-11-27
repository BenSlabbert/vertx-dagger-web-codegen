/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator.ioc;

import github.benslabbert.vdw.codegen.annotation.ioc.GenerateModuleBindings;
import github.benslabbert.vdw.codegen.generator.GenerationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

public class ModuleBindingsGenerator extends AbstractProcessor {

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
    return Set.of(GenerateModuleBindings.class.getCanonicalName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      Map<String, Set<String>> moduleBindings = new HashMap<>();
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
        String canonicalName = element.asType().toString();
        String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
        Name simpleName = element.getSimpleName();
        moduleBindings.compute(
            classPackage,
            (key, oldValue) -> {
              if (null == oldValue) {
                Set<String> set = new HashSet<>();
                set.add(simpleName.toString());
                return set;
              }
              oldValue.add(simpleName.toString());
              return oldValue;
            });
      }

      generateAllModuleBindings(moduleBindings);
      return !moduleBindings.isEmpty();
    }

    return false;
  }

  private void generateAllModuleBindings(Map<String, Set<String>> moduleBindings) {
    moduleBindings.entrySet().forEach(this::printModuleBindings);
  }

  private void printModuleBindings(Map.Entry<String, Set<String>> entry) {
    String packageName = entry.getKey();

    String allBindings =
        entry.getValue().stream().map(s -> s + ".class").collect(Collectors.joining(","));

    try {
      String interfaceName = "GeneratedModuleBindings";
      JavaFileObject sourceFile =
          processingEnv.getFiler().createSourceFile(packageName + "." + interfaceName);

      try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
        writer.printf(
            """
            package %s;

            import jakarta.annotation.Generated;
            import dagger.Module;

            @Generated(value = "%s", date = "%s")
            @Module(
                includes = {
                  %s
                })
            interface %s {}
            """,
            packageName,
            getClass().getCanonicalName(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
            allBindings,
            interfaceName);
      }
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }
}
