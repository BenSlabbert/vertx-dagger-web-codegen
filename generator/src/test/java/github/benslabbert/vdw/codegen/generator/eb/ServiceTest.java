/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator.eb;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.google.testing.compile.JavaSourcesSubject;
import java.net.URL;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ServiceTest {

  @ParameterizedTest
  @CsvSource(
      useHeadersInDisplayName = true,
      delimiter = '|',
      textBlock =
"""
Source File                             | skipFmt

ExampleService.java                     | true
ExampleService.java                     | false
ExampleServiceNoAuth.java               | true
ExampleServiceNoAuth.java               | false
ExampleServiceNoAuthNoValidator.java    | true
ExampleServiceNoAuthNoValidator.java    | false
""")
  void test(String file, boolean skipFmt) {
    URL resource = this.getClass().getClassLoader().getResource(file);
    assertThat(resource).isNotNull();

    JavaSourcesSubject.SingleSourceAdapter sourceAdapter =
        assertAbout(JavaSourceSubjectFactory.javaSource())
            .that(JavaFileObjects.forResource(resource));

    if (skipFmt) {
      sourceAdapter.withCompilerOptions("-AskipFmt");
    }

    sourceAdapter.processedWith(new VertxEBProxyGenerators()).compilesWithoutError();
  }
}
