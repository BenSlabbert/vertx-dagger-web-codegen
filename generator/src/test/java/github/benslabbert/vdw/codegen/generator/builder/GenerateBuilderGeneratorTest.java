/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator.builder;

import static com.google.common.truth.Truth.assertThat;

import com.google.auto.value.processor.AutoBuilderProcessor;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import java.net.URL;
import org.junit.jupiter.api.Test;

class GenerateBuilderGeneratorTest {

  @Test
  void generatesInitializerWhenTablePresent() {
    URL resource = this.getClass().getClassLoader().getResource("PersonWithTable.java");
    assertThat(resource).isNotNull();

    com.google.common.truth.Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new GenerateBuilderGenerator(), new AutoBuilderProcessor())
        .compilesWithoutError();
  }

  @Test
  void compilesWhenNoTable() {
    URL resource = this.getClass().getClassLoader().getResource("PersonNoTable.java");
    assertThat(resource).isNotNull();

    com.google.common.truth.Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(JavaFileObjects.forResource(resource))
        .processedWith(new GenerateBuilderGenerator(), new AutoBuilderProcessor())
        .compilesWithoutError();
  }
}
