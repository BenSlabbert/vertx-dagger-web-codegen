/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

abstract class ProcessorBase extends AbstractProcessor {

  private final Set<String> supportedAnnotationTypes;

  ProcessorBase(Set<String> supportedAnnotationTypes) {
    this.supportedAnnotationTypes = supportedAnnotationTypes;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return supportedAnnotationTypes;
  }

  private static class StringSource extends CharSource {

    private final StringWriter writer;

    private StringSource(StringWriter writer) {
      this.writer = writer;
    }

    @Nonnull
    @Override
    public Reader openStream() {
      return new StringReader(writer.toString());
    }
  }

  private static class FileSink extends CharSink {

    private final JavaFileObject builderFile;

    private FileSink(JavaFileObject builderFile) {
      this.builderFile = builderFile;
    }

    @Nonnull
    @Override
    public Writer openStream() throws IOException {
      return new PrintWriter(builderFile.openWriter());
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) {
      return false;
    }

    for (var annotation : annotations) {
      for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        try {
          List<GeneratedFile> files = generateTempFile(element);
          if (files.isEmpty()) {
            continue;
          }

          for (GeneratedFile file : files) {
            formatFile(file);
          }
        } catch (Exception e) {
          throw new GenerationException(e);
        }
      }
    }
    return true;
  }

  private void formatFile(GeneratedFile generatedFile) {
    try {
      System.err.println(generatedFile.stringWriter());
      CharSource source = new StringSource(generatedFile.stringWriter());
      JavaFileObject builderFile =
          processingEnv.getFiler().createSourceFile(generatedFile.realFileName());
      CharSink output = new FileSink(builderFile);

      JavaFormatterOptions options =
          JavaFormatterOptions.builder()
              .formatJavadoc(true)
              .reorderModifiers(true)
              .style(JavaFormatterOptions.Style.GOOGLE)
              .build();

      new Formatter(options).formatSource(source, output);
    } catch (FormatterException | IOException e) {
      throw new GenerationException(e);
    }
  }

  void printNote(CharSequence cs) {
    processingEnv.getMessager().printNote(cs);
  }

  void printError(CharSequence cs) {
    processingEnv.getMessager().printError(cs);
  }

  void printError(CharSequence cs, Element e) {
    processingEnv.getMessager().printError(cs, e);
  }

  void printNote(CharSequence cs, Element element) {
    processingEnv.getMessager().printNote(cs, element);
  }

  abstract List<GeneratedFile> generateTempFile(Element element);

  record GeneratedFile(StringWriter stringWriter, String realFileName) {}
}
