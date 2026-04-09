/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.gradle;

import github.benslabbert.vdw.codegen.extractor.core.AdviceMerger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

/**
 * Merges {@code META-INF/advice_annotations} files from all dependency JARs with the
 * annotation-processor-generated file for the current module.
 *
 * <p>This is the Gradle equivalent of the Maven {@code advice-extractor-plugin}'s {@code
 * merge-advices} goal, which runs at the {@code PROCESS_CLASSES} phase (after compile, before
 * package).
 *
 * <h3>Output location</h3>
 *
 * The merged file is written to a staging directory ({@code build/tmp/mergeAdvices/}) rather than
 * overwriting the annotation-processor output in-place. The {@code jar} task is then configured to
 * exclude the raw AP file and include the merged staging file instead.
 *
 * <p>Using a separate output file is essential for correct Gradle UP-TO-DATE semantics:
 *
 * <ul>
 *   <li>{@code annotationProcessorAdviceFile} ({@code @InputFile}) always contains the <em>pure AP
 *       output</em> – it is never written by this task, so its fingerprint only changes when {@code
 *       compileJava} regenerates it.
 *   <li>{@code classpathJars} ({@code @Classpath}) spans the full runtime classpath. Any JAR change
 *       in any scope (including {@code runtimeOnly}) marks this task out-of-date.
 *   <li>Because the AP file is not the {@code @OutputFile}, there is no input/output cycle. When
 *       only a runtime dependency changes – and {@code compileJava} therefore does not re-run –
 *       this task still reads the unmodified, pure AP content and produces the correct merged
 *       result.
 * </ul>
 */
@DisableCachingByDefault(because = "Classpath scanning makes remote caching impractical")
public abstract class MergeAdvicesTask extends DefaultTask {

  /**
   * The annotation-processor-generated {@code META-INF/advice_annotations} file. Lives inside
   * {@code compileJava}'s destination directory and is declared as an input only – it is never
   * modified by this task, so its fingerprint always reflects the pure AP output.
   *
   * <p>Marked {@code @Optional} because the AP only creates this file when the current module
   * contains at least one {@code @BeforeAdvice} or {@code @AroundAdvice} annotation.
   */
  @InputFile
  @Optional
  @PathSensitive(PathSensitivity.NONE)
  public abstract RegularFileProperty getAnnotationProcessorAdviceFile();

  /**
   * The full runtime classpath to scan for {@code META-INF/advice_annotations} entries in
   * dependency JARs. Using {@code runtimeClasspath} (a superset of {@code compileClasspath})
   * ensures that a change to <em>any</em> dependency – including {@code runtimeOnly} ones –
   * triggers a re-run of this task.
   */
  @InputFiles
  @Classpath
  public abstract ConfigurableFileCollection getClasspathJars();

  /**
   * Staging output location for the fully merged file. Must differ from {@link
   * #getAnnotationProcessorAdviceFile()} to avoid an input/output cycle and to guarantee correct
   * UP-TO-DATE behaviour when only runtime dependencies change.
   */
  @OutputFile
  public abstract RegularFileProperty getMergedAdviceFile();

  /** Name of the advice annotations file inside {@code META-INF} (without any path prefix). */
  @Input
  public abstract Property<String> getAdviceFileName();

  @TaskAction
  public void mergeAdvices() throws IOException {
    String fileName = getAdviceFileName().getOrElse("advice_annotations");

    File apFile =
        getAnnotationProcessorAdviceFile().isPresent()
            ? getAnnotationProcessorAdviceFile().get().getAsFile()
            : null;

    LinkedHashSet<String> entries =
        AdviceMerger.merge(apFile, getClasspathJars().getFiles(), fileName);

    getLogger()
        .info(
            "Merging advices; total {} unique entries from AP output and dependency JARs",
            entries.size());

    File outputFile = getMergedAdviceFile().get().getAsFile();
    outputFile.getParentFile().mkdirs();
    Files.writeString(outputFile.toPath(), AdviceMerger.format(entries));

    getLogger().info("Merged advices written to: {}", outputFile.getAbsolutePath());
  }
}
