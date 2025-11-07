/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.bytebuddy.build.BuildLogger;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;

public class TransformerPlugin implements Plugin {

  private static final String ADVICE_ANNOTATION_FILE = "META-INF/advice_annotations";

  private final BuildLogger log;
  private final List<Junction<AnnotationSource>> matchers;

  public TransformerPlugin(File file, BuildLogger logger) {
    this.log = logger;
    log.info("init transformer: " + file);
    List<String> advices = loadAdvices(file);
    for (String advice : advices) {
      log.info("advices loaded: " + advice);
    }
    this.matchers = advices.stream().map(s -> isAnnotatedWith(named(s))).toList();
  }

  @Override
  public Builder<?> apply(Builder<?> builder, TypeDescription target, ClassFileLocator clf) {
    //      for (InDefinedShape tm : target.getDeclaredMethods()) {
    //      builder = builder.method(md -> md.equals(tm)).intercept(Advice.to(String.class));
    //    }
    log.info("transform advices: " + target.getName());

    return builder;
  }

  @Override
  public void close() {
    // nothing open
    log.info("close transformer");
  }

  @Override
  public boolean matches(TypeDescription typeDefinitions) {
    if (matchers.isEmpty()) {
      return false;
    }

    return typeDefinitions.getDeclaredMethods().stream()
        .anyMatch(shape -> matchers.stream().anyMatch(m -> m.matches(shape)));
  }

  private List<String> loadAdvices(File file) {
    log.info("Loading advices");

    Path annotationFile = file.toPath().resolve(ADVICE_ANNOTATION_FILE);
    if (!annotationFile.toFile().exists()) {
      log.info("annotation file does not exist");
      return List.of();
    }

    try {
      return Files.readAllLines(annotationFile).stream()
          .filter(line -> !line.isEmpty())
          .map(String::trim)
          .filter(line -> !line.startsWith("#"))
          .map(String::trim)
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
