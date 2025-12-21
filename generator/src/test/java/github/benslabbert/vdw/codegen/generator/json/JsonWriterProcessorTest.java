/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator.json;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import java.net.URL;
import java.util.stream.Stream;
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
}
