/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import java.net.URL;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ServiceTest {

  @ParameterizedTest
  @CsvSource({
    "ExampleService.java",
    "ExampleServiceNoAuth.java",
    "ExampleServiceNoAuthNoValidator.java"
  })
  void test(String file) {
    URL resource = this.getClass().getClassLoader().getResource(file);
    assertThat(resource).isNotNull();

    assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new VertxEBProxyGenerators())
        .compilesWithoutError();
  }
}
