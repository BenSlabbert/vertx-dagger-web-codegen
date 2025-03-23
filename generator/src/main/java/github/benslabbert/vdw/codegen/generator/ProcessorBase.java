/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
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
  private Path formatterPath;

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

  private void init() {
    try {
      printNote("copying formatter binary");
      InputStream formatterStream = getClass().getClassLoader().getResourceAsStream("formatter");
      if (null == formatterStream) {
        throw new GenerationException("unable to load formatter");
      }

      formatterPath = Files.createTempFile("formatter-", "");
      printNote("formatterPath: " + formatterPath);
      Files.copy(formatterStream, formatterPath, StandardCopyOption.REPLACE_EXISTING);
      Set<PosixFilePermission> perms = new HashSet<>();
      perms.add(PosixFilePermission.OWNER_READ);
      perms.add(PosixFilePermission.OWNER_WRITE);
      perms.add(PosixFilePermission.OWNER_EXECUTE);
      Files.setPosixFilePermissions(formatterPath, perms);
    } catch (Exception e) {
      printError(getClass().getCanonicalName() + " init exception: " + e);
      throw new GenerationException(e);
    }
  }

  record GeneratedFile(Path tempFile, String realFileName) {}

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) {
      return false;
    }

    init();

    for (var annotation : annotations) {
      for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        try {
          List<GeneratedFile> files = generateTempFile(element);
          if (files.isEmpty()) {
            continue;
          }

          for (GeneratedFile generatedFile : files) {
            String absPath = generatedFile.tempFile().toAbsolutePath().toString();
            printNote("formatting file absPath: " + absPath, element);

            ProcessBuilder processBuilder =
                new ProcessBuilder(formatterPath.toAbsolutePath().toString(), "-i", absPath);
            Process process = processBuilder.start();
            printNote("process error stream", element);
            print(process.getErrorStream(), element);
            printNote("process normal stream", element);
            print(process.getInputStream(), element);

            JavaFileObject builderFile =
                processingEnv.getFiler().createSourceFile(generatedFile.realFileName());
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
              for (String line : Files.readAllLines(generatedFile.tempFile())) {
                out.println(line);
              }
            }

            Files.deleteIfExists(generatedFile.tempFile());
          }
        } catch (Exception e) {
          throw new GenerationException(e);
        }
      }
    }

    // delete temp file
    try {
      if (null != formatterPath) {
        Files.deleteIfExists(formatterPath);
      }
    } catch (IOException e) {
      // swallow
    }
    return true;
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

  private void print(InputStream is, Element element) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder builder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      builder.append(line);
      builder.append(System.getProperty("line.separator"));
    }
    String result = builder.toString();
    printNote(result, element);
  }

  abstract List<GeneratedFile> generateTempFile(Element element) throws Exception;
}
