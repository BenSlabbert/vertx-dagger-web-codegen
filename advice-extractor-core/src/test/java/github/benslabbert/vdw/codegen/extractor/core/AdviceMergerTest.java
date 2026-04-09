/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.extractor.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AdviceMergerTest {

  @TempDir File tempDir;

  // ---------------------------------------------------------------------------
  // merge()
  // ---------------------------------------------------------------------------

  @Test
  void merge_withNullApFile_returnsEmptySet() {
    LinkedHashSet<String> result = AdviceMerger.merge(null, List.of(), "advice_annotations");
    assertThat(result).isEmpty();
  }

  @Test
  void merge_withNonExistentApFile_returnsEmptySet() {
    File missing = new File(tempDir, "does_not_exist");
    LinkedHashSet<String> result = AdviceMerger.merge(missing, List.of(), "advice_annotations");
    assertThat(result).isEmpty();
  }

  @Test
  void merge_readsEntriesFromApFile() throws IOException {
    File apFile = writeFile("com.example.FooAdvice\ncom.example.BarAdvice\n");
    LinkedHashSet<String> result = AdviceMerger.merge(apFile, List.of(), "advice_annotations");
    assertThat(result).containsExactly("com.example.FooAdvice", "com.example.BarAdvice");
  }

  @Test
  void merge_stripsBlankLinesAndComments() throws IOException {
    File apFile =
        writeFile(
            "# this is a comment\n\ncom.example.Alpha\n  \n# another comment\ncom.example.Beta\n");
    LinkedHashSet<String> result = AdviceMerger.merge(apFile, List.of(), "advice_annotations");
    assertThat(result).containsExactly("com.example.Alpha", "com.example.Beta");
  }

  @Test
  void merge_deduplicatesEntriesAcrossApFileAndJars() throws IOException {
    File apFile = writeFile("com.example.Shared\ncom.example.OnlyInAp\n");
    File jar = buildJar("META-INF/advice_annotations", "com.example.Shared\ncom.example.InJar\n");

    LinkedHashSet<String> result = AdviceMerger.merge(apFile, List.of(jar), "advice_annotations");
    // "com.example.Shared" must appear only once; order: AP entries first, then jar entries.
    assertThat(result)
        .containsExactly("com.example.Shared", "com.example.OnlyInAp", "com.example.InJar");
  }

  @Test
  void merge_scansMultipleJars() throws IOException {
    File jar1 = buildJar("META-INF/advice_annotations", "com.example.A\n");
    File jar2 = buildJar("META-INF/advice_annotations", "com.example.B\n");

    LinkedHashSet<String> result =
        AdviceMerger.merge(null, List.of(jar1, jar2), "advice_annotations");
    assertThat(result).containsExactlyInAnyOrder("com.example.A", "com.example.B");
  }

  @Test
  void merge_skipsJarWithNoMatchingEntry() throws IOException {
    File jar = buildJar("META-INF/other_file", "irrelevant\n");

    LinkedHashSet<String> result = AdviceMerger.merge(null, List.of(jar), "advice_annotations");
    assertThat(result).isEmpty();
  }

  @Test
  void merge_ignoresNonJarFiles() throws IOException {
    File notAJar = writeFile("com.example.ShouldNotBeSeen\n");

    LinkedHashSet<String> result = AdviceMerger.merge(null, List.of(notAJar), "advice_annotations");
    assertThat(result).isEmpty();
  }

  @Test
  void merge_respectsCustomAdviceFileName() throws IOException {
    File jar = buildJar("META-INF/custom_advice", "com.example.Custom\n");

    LinkedHashSet<String> withCustomName = AdviceMerger.merge(null, List.of(jar), "custom_advice");
    assertThat(withCustomName).containsExactly("com.example.Custom");

    LinkedHashSet<String> withDefaultName =
        AdviceMerger.merge(null, List.of(jar), "advice_annotations");
    assertThat(withDefaultName).isEmpty();
  }

  // ---------------------------------------------------------------------------
  // format()
  // ---------------------------------------------------------------------------

  @Test
  void format_emptySetReturnsEmptyString() {
    assertThat(AdviceMerger.format(new LinkedHashSet<>())).isEmpty();
  }

  @Test
  void format_eachEntryFollowedByLineSeparator() {
    var entries = new LinkedHashSet<>(List.of("com.example.A", "com.example.B"));
    String result = AdviceMerger.format(entries);
    assertThat(result)
        .isEqualTo(
            "com.example.A" + System.lineSeparator() + "com.example.B" + System.lineSeparator());
  }

  // ---------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------

  private File writeFile(String content) throws IOException {
    File f = new File(tempDir, "ap_" + System.nanoTime() + ".txt");
    Files.writeString(f.toPath(), content, StandardCharsets.UTF_8);
    return f;
  }

  /**
   * Builds an in-memory JAR containing a single entry {@code entryPath} with {@code content} and
   * writes it to a temp file with a {@code .jar} extension.
   */
  private File buildJar(String entryPath, String content) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (JarOutputStream jos = new JarOutputStream(baos)) {
      jos.putNextEntry(new JarEntry(entryPath));
      jos.write(content.getBytes(StandardCharsets.UTF_8));
      jos.closeEntry();
    }
    File jar = new File(tempDir, "test_" + System.nanoTime() + ".jar");
    Files.write(jar.toPath(), baos.toByteArray());
    return jar;
  }
}
