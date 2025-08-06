/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.google.testing.compile.JavaSourcesSubject;
import java.net.URL;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TableGeneratorTest {

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
true
false
""")
  void test(boolean skipFmt) {
    URL resource = this.getClass().getClassLoader().getResource("Person.java");
    assertThat(resource).isNotNull();

    JavaSourcesSubject.SingleSourceAdapter sourceAdapter =
        assertAbout(JavaSourceSubjectFactory.javaSource())
            .that(JavaFileObjects.forResource(resource));

    if (skipFmt) {
      sourceAdapter.withCompilerOptions("-AskipFmt");
    }

    sourceAdapter.processedWith(new TableGenerator()).compilesWithoutError();
  }
}
