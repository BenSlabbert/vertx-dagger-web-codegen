/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.extractor.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * Build-tool-agnostic utility for merging {@code META-INF/advice_annotations} entries.
 *
 * <p>Both the Maven {@code advice-extractor-maven-plugin} and the Gradle {@code
 * advice-extractor-gradle-plugin} delegate to this class so that the merge semantics are kept in
 * one place and free of any build-tool API dependencies.
 */
public final class AdviceMerger {

  private static final Logger log = Logger.getLogger(AdviceMerger.class.getName());

  private AdviceMerger() {}

  /**
   * Merges advice annotation entries from an annotation-processor output file and a collection of
   * dependency JARs.
   *
   * <p>Lines that are blank or start with {@code #} are treated as comments and skipped. The
   * returned set preserves insertion order and silently de-duplicates identical entries.
   *
   * @param apOutputFile the annotation-processor-generated file; may be {@code null} or
   *     non-existent
   * @param classpathJars JAR files to scan for {@code META-INF/<adviceFileName>} entries
   * @param adviceFileName the file name inside {@code META-INF} (e.g., {@code advice_annotations})
   * @return a deduplicated, insertion-ordered set of advice class names
   */
  public static LinkedHashSet<String> merge(
      File apOutputFile, Iterable<File> classpathJars, String adviceFileName) {

    // LinkedHashSet preserves insertion order and de-duplicates silently.
    var entries = new LinkedHashSet<String>();
    String metaInfPath = "META-INF/" + adviceFileName;

    // 1. Seed from the AP-generated file (pure AP output, never modified by this method).
    if (apOutputFile != null && apOutputFile.exists()) {
      try {
        var lines = Files.readAllLines(apOutputFile.toPath(), StandardCharsets.UTF_8);
        for (var line : lines) {
          var trimmed = line.trim();
          if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
            entries.add(trimmed);
          }
        }
        log.fine(
            () -> "Loaded " + entries.size() + " entry/entries from annotation processor output");
      } catch (IOException e) {
        log.warning("Failed to read AP output file: " + apOutputFile.getAbsolutePath() + " - " + e);
      }
    }

    // 2. Scan each dependency JAR for META-INF/<adviceFileName>.
    int jarsWithAdvices = 0;
    for (File jar : classpathJars) {
      if (!jar.exists() || !jar.getName().endsWith(".jar")) {
        continue;
      }
      try (JarFile jarFile = new JarFile(jar)) {
        JarEntry entry = jarFile.getJarEntry(metaInfPath);
        if (entry == null) {
          continue;
        }
        log.fine(() -> "Found " + adviceFileName + " in " + jar.getName());
        try (var reader =
            new BufferedReader(
                new InputStreamReader(jarFile.getInputStream(entry), StandardCharsets.UTF_8))) {
          String line;
          while ((line = reader.readLine()) != null) {
            var trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
              entries.add(trimmed);
            }
          }
        }
        jarsWithAdvices++;
      } catch (IOException e) {
        log.warning("Failed to scan JAR: " + jar.getAbsolutePath() + " - " + e);
      }
    }

    int finalJarsWithAdvices = jarsWithAdvices;
    log.fine(
        () ->
            "Merged "
                + finalJarsWithAdvices
                + " dep advice file(s); total "
                + entries.size()
                + " unique entries");

    return entries;
  }

  /**
   * Formats a set of entries as a newline-separated string suitable for writing to an advice file.
   *
   * @param entries the entries to format
   * @return the formatted string (each entry followed by the system line separator), or an empty
   *     string if there are no entries
   */
  public static String format(LinkedHashSet<String> entries) {
    if (entries.isEmpty()) {
      return "";
    }
    var sb = new StringBuilder();
    for (var entry : entries) {
      sb.append(entry).append(System.lineSeparator());
    }
    return sb.toString();
  }
}
