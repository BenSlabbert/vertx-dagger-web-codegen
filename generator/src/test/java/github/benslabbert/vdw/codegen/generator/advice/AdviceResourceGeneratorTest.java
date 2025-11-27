/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator.advice;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.google.testing.compile.JavaSourcesSubject;
import java.net.URL;
import org.junit.jupiter.api.Test;

class AdviceResourceGeneratorTest {

  @Test
  void before() {
    URL resource = this.getClass().getClassLoader().getResource("LogEntry.java");
    assertThat(resource).isNotNull();

    JavaSourcesSubject.SingleSourceAdapter sourceAdapter =
        assertAbout(JavaSourceSubjectFactory.javaSource())
            .that(JavaFileObjects.forResource(resource));

    sourceAdapter.processedWith(new AdviceResourceGenerator()).compilesWithoutError();
  }

  @Test
  void around() {
    URL resource = this.getClass().getClassLoader().getResource("Observed.java");
    assertThat(resource).isNotNull();

    JavaSourcesSubject.SingleSourceAdapter sourceAdapter =
        assertAbout(JavaSourceSubjectFactory.javaSource())
            .that(JavaFileObjects.forResource(resource));

    sourceAdapter.processedWith(new AdviceResourceGenerator()).compilesWithoutError();
  }
}
