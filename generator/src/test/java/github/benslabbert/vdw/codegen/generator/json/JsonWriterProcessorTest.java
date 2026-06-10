/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator.json;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class JsonWriterProcessorTest {

  static Stream<String> source() {
    return Stream.of("Example.java", "Primitive.java");
  }

  @ParameterizedTest
  @MethodSource("source")
  void example(String className) {
    URL resource = this.getClass().getClassLoader().getResource(className);
    assertThat(resource).isNotNull();

    assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new JsonWriterProcessor())
        .compilesWithoutError();
  }

  @Test
  void notEmptyExample() {
    URL resource = this.getClass().getClassLoader().getResource("NotEmptyExample.java");
    assertThat(resource).isNotNull();

    assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new JsonWriterProcessor())
        .compilesWithoutError();
  }

  @Test
  void nested() {
    URL resource = this.getClass().getClassLoader().getResource("Nested.java");
    assertThat(resource).isNotNull();

    assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new JsonWriterProcessor())
        .compilesWithoutError();
  }

  @Test
  void nestedCollection() {
    URL resource = this.getClass().getClassLoader().getResource("FindAllResponse.java");
    assertThat(resource).isNotNull();

    assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new JsonWriterProcessor())
        .compilesWithoutError();
  }

  @Test
  void nestedSetCollection() {
    URL resource = this.getClass().getClassLoader().getResource("SetCollectionResponse.java");
    assertThat(resource).isNotNull();

    assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new JsonWriterProcessor())
        .compilesWithoutError();
  }

  @Test
  void nestedCollectionInterface() {
    URL resource = this.getClass().getClassLoader().getResource("CollectionResponse.java");
    assertThat(resource).isNotNull();

    assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new JsonWriterProcessor())
        .compilesWithoutError();
  }

  @Test
  void nullable() throws Exception {
    URL resource = this.getClass().getClassLoader().getResource("NullableExample.java");
    assertThat(resource).isNotNull();

    Compilation compilation =
        Compiler.javac()
            .withProcessors(new JsonWriterProcessor())
            .compile(JavaFileObjects.forResource(resource));

    assertThat(compilation.status()).isEqualTo(Compilation.Status.SUCCESS);

    Optional<JavaFileObject> generatedSource =
        compilation.generatedSourceFile("my.test.NullableExampleJson");
    assertThat(generatedSource.isPresent()).isTrue();

    try (URLClassLoader classLoader = compileAndLoad(compilation)) {
      Class<?> nullableExampleClass = classLoader.loadClass("my.test.NullableExample");
      Class<?> nullableExampleJsonClass = classLoader.loadClass("my.test.NullableExampleJson");
      Class<?> nullableExampleInnerClass = classLoader.loadClass("my.test.NullableExample$Inner");

      Object allNulls =
          nullableExampleClass.getDeclaredConstructors()[0].newInstance(
              null, null, null, null, null, null);
      JsonObject jsonWithAllNulls =
          (JsonObject)
              nullableExampleJsonClass
                  .getDeclaredMethod("toJson", nullableExampleClass)
                  .invoke(null, allNulls);

      assertThat(jsonWithAllNulls.fieldNames()).isEmpty();

      Object innerWithNullName =
          nullableExampleInnerClass.getDeclaredConstructors()[0].newInstance((Object) null);
      Object withInner =
          nullableExampleClass.getDeclaredConstructors()[0].newInstance(
              null,
              null,
              null,
              LocalDateTime.parse("2026-06-10T16:00:00"),
              null,
              innerWithNullName);

      JsonObject jsonWithInner =
          (JsonObject)
              nullableExampleJsonClass
                  .getDeclaredMethod("toJson", nullableExampleClass)
                  .invoke(null, withInner);

      assertThat(jsonWithInner.containsKey("time")).isTrue();
      assertThat(jsonWithInner.containsKey("inner")).isTrue();
      assertThat(jsonWithInner.getJsonObject("inner").fieldNames()).isEmpty();
      assertThat(jsonWithInner.containsKey("name")).isFalse();
      assertThat(jsonWithInner.containsKey("value")).isFalse();
      assertThat(jsonWithInner.containsKey("date")).isFalse();
      assertThat(jsonWithInner.containsKey("offsetDateTime")).isFalse();
    }
  }

  private static URLClassLoader compileAndLoad(Compilation compilation) throws IOException {
    Path sourcesDirectory = Files.createTempDirectory("nullable-json-sources");
    Path classesDirectory = Files.createTempDirectory("nullable-json-classes");

    List<Path> sources = new ArrayList<>();
    for (JavaFileObject sourceFile : compilation.sourceFiles()) {
      Path path = sourcesDirectory.resolve(sourcePath(sourceFile.getName()));
      Files.writeString(path, sourceFile.getCharContent(true), StandardCharsets.UTF_8);
      sources.add(path);
    }
    for (JavaFileObject sourceFile : compilation.generatedSourceFiles()) {
      Path path = sourcesDirectory.resolve(sourcePath(sourceFile.getName()));
      Files.writeString(path, sourceFile.getCharContent(true), StandardCharsets.UTF_8);
      sources.add(path);
    }

    JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
    assertThat(javaCompiler).isNotNull();

    try (StandardJavaFileManager fileManager =
        javaCompiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)) {
      boolean success =
          javaCompiler
              .getTask(
                  null,
                  fileManager,
                  null,
                  List.of(
                      "-classpath",
                      System.getProperty("java.class.path"),
                      "-d",
                      classesDirectory.toString()),
                  null,
                  fileManager.getJavaFileObjectsFromPaths(sources))
              .call();
      assertThat(success).isTrue();
    }

    return new URLClassLoader(
        new URL[] {classesDirectory.toUri().toURL()},
        JsonWriterProcessorTest.class.getClassLoader());
  }

  private static String sourcePath(String sourceName) {
    int separatorIndex = sourceName.lastIndexOf('/');
    if (separatorIndex < 0) {
      return sourceName;
    }
    return sourceName.substring(separatorIndex + 1);
  }
}
