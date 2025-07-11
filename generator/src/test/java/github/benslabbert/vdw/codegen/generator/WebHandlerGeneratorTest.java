/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import java.net.URL;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WebHandlerGeneratorTest {

  @ParameterizedTest
  @CsvSource({
    "ExampleHandler.java",
    "ExampleNoAuthHandler.java",
    "ExampleNoAuthNoValidationHandler.java"
  })
  void test(String file) {
    URL resource = this.getClass().getClassLoader().getResource(file);
    assertThat(resource).isNotNull();

    assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new WebHandlerGenerator())
        .compilesWithoutError();
  }
}
