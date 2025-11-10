/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.advicetransformer;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

import github.benslabbert.vdw.codegen.annotation.AlreadyTransformed;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.build.BuildLogger;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.SuperMethodCall;

public class TransformerPlugin implements Plugin {

  private static final String ADVICE_ANNOTATION_FILE = "META-INF/advice_annotations";

  private final Map<AdvicePair, Junction<AnnotationSource>> matchersMap;
  private final BuildLogger log;

  private record AdvicePair(String annotation, String implementation, Type type) {

    Junction<TypeDescription> matcher() {
      return named(annotation);
    }

    enum Type {
      BEFORE,
      AROUND
    }

    static Type fromString(String type) {
      return switch (type.toUpperCase()) {
        case "BEFORE" -> Type.BEFORE;
        case "AROUND" -> Type.AROUND;
        default -> throw new IllegalArgumentException("unknown type " + type);
      };
    }
  }

  public TransformerPlugin(File file, BuildLogger logger) {
    this.log = logger;
    log.info("init transformer: " + file);
    Set<AdvicePair> advices = loadAdvices(file);
    for (AdvicePair advice : advices) {
      log.info("advices loaded: " + advice);
    }
    this.matchersMap =
        advices.stream().collect(toMap(identity(), a -> isAnnotatedWith(a.matcher())));
  }

  record MatchedShape(MethodDescription.InDefinedShape shape, Set<AdvicePair> advices) {}

  @Override
  public Builder<?> apply(Builder<?> builder, TypeDescription target, ClassFileLocator clf) {
    log.info("transform advices: " + target.getName());

    List<MatchedShape> matchedAdvices =
        target.getDeclaredMethods().stream()
            .filter(this::shapeMatches)
            .map(
                shape ->
                    new MatchedShape(
                        shape,
                        matchersMap.entrySet().stream()
                            .filter(m -> m.getValue().matches(shape))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet())))
            .toList();

    for (MatchedShape ma : matchedAdvices) {
      boolean even = System.currentTimeMillis() % 2 == 0;
      String impl = even ? "intercept" : "bind";

      String advices =
          ma.advices.stream().map(AdvicePair::annotation).collect(Collectors.joining(","));

      // todo we can add an priority to our advice annotations
      //  then we can ensure that we apply the before advices in their specific priority
      //  if the priority value does not exist, catch the IllegalArgumentException and assign the
      //  default value
      //  AnnotationDescription annotationDescription = ma.shape.getDeclaredAnnotations().get(0);
      //  AnnotationValue<?, ?> priority = annotationDescription.getValue("priority");
      //  Integer priorityValue = priority.resolve(int.class);

      if (even) {
        builder =
            builder
                .method(md -> md.equals(ma.shape))
                .intercept(
                    Advice.withCustomMapping()
                        .bind(AdviceName.class, advices)
                        .to(ApplyAdvice.class))
                .annotateMethod(alreadyTransformed(impl));
      } else {
        builder =
            builder
                .method(md -> md.equals(ma.shape))
                .intercept(SuperMethodCall.INSTANCE)
                .annotateMethod(alreadyTransformed(impl))
                .visit(
                    Advice.withCustomMapping()
                        .bind(AdviceName.class, advices)
                        .to(ApplyAdvice.class)
                        .on(md -> md.equals(ma.shape)));
      }
    }

    return builder;
  }

  private static AnnotationDescription alreadyTransformed(String impl) {
    return AnnotationDescription.Builder.ofType(AlreadyTransformed.class)
        .define("value", impl)
        .build();
  }

  @Override
  public void close() {
    // nothing open
    log.info("close transformer");
  }

  @Override
  public boolean matches(TypeDescription target) {
    if (matchersMap.isEmpty()) {
      return false;
    }

    return target.getDeclaredMethods().stream().anyMatch(this::shapeMatches);
  }

  private boolean shapeMatches(MethodDescription.InDefinedShape shape) {
    if (shape.getDeclaredAnnotations().isAnnotationPresent(AlreadyTransformed.class)) {
      return false;
    }

    return matchersMap.values().stream().anyMatch(m -> m.matches(shape));
  }

  private Set<AdvicePair> loadAdvices(File file) {
    log.info("Loading advices");

    Path annotationFile = file.toPath().resolve(ADVICE_ANNOTATION_FILE);
    if (!annotationFile.toFile().exists()) {
      log.info("annotation file does not exist");
      return Set.of();
    }

    try {
      return Files.readAllLines(annotationFile).stream()
          .filter(line -> !line.isEmpty())
          .map(String::trim)
          .filter(line -> !line.startsWith("#"))
          .map(String::trim)
          .map(
              s -> {
                String[] split = s.split(",");
                if (split.length != 3) {
                  throw new IllegalArgumentException("advice_annotation entry must have 3 columns");
                }
                return new AdvicePair(split[0], split[1], AdvicePair.fromString(split[2]));
              })
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
