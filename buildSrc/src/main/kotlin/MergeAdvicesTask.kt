/* Licensed under Apache-2.0 2025. */
import java.util.jar.JarFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Merges META-INF/advice_annotations files from all dependency JARs with the annotation-processor
 * generated META-INF/advice_annotations for the current module.
 *
 * <p>This is the Gradle equivalent of the Maven advice-extractor-plugin's {@code merge-advices}
 * goal, which runs at {@code PROCESS_CLASSES} phase (after compile, before package).
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
 *   <li>{@code annotationProcessorAdviceFile} ({@code @InputFile}) always contains the <em>pure
 *       AP output</em> – it is never written by this task, so its fingerprint only changes when
 *       {@code compileJava} regenerates it.
 *   <li>{@code classpathJars} ({@code @Classpath}) spans the full runtime classpath. Any JAR
 *       change in any scope (including {@code runtimeOnly}) marks this task out-of-date.
 *   <li>Because the AP file is not the {@code @OutputFile}, there is no input/output cycle. When
 *       only a runtime dependency changes – and {@code compileJava} therefore does not re-run –
 *       this task still reads the unmodified, pure AP content and produces the correct merged
 *       result.
 * </ul>
 */
abstract class MergeAdvicesTask : DefaultTask() {

  /**
   * The annotation-processor-generated {@code META-INF/advice_annotations} file. Lives inside
   * {@code compileJava}'s destination directory and is declared as an input only – it is never
   * modified by this task, so its fingerprint always reflects the pure AP output.
   *
   * <p>Marked {@code @Optional} because the AP only creates this file when the current module
   * contains at least one {@code @BeforeAdvice} or {@code @AroundAdvice} annotation.
   */
  @get:InputFile @get:Optional abstract val annotationProcessorAdviceFile: RegularFileProperty

  /**
   * The full runtime classpath to scan for {@code META-INF/advice_annotations} entries in
   * dependency JARs. Using {@code runtimeClasspath} (a superset of {@code compileClasspath})
   * ensures that a change to <em>any</em> dependency – including {@code runtimeOnly} ones –
   * triggers a re-run of this task.
   */
  @get:InputFiles @get:Classpath abstract val classpathJars: ConfigurableFileCollection

  /**
   * Staging output location for the fully merged file. Must differ from {@link
   * #annotationProcessorAdviceFile} to avoid an input/output cycle and to guarantee correct
   * UP-TO-DATE behaviour when only runtime dependencies change.
   */
  @get:OutputFile abstract val mergedAdviceFile: RegularFileProperty

  /** Name of the advice annotations file inside {@code META-INF} (without any path prefix). */
  @get:Input abstract val adviceFileName: Property<String>

  init {
    adviceFileName.convention("advice_annotations")
  }

  @TaskAction
  fun mergeAdvices() {
    val fileName = adviceFileName.get()
    val metaInfPath = "META-INF/$fileName"

    // LinkedHashSet preserves insertion order and silently de-duplicates identical entries.
    val entries = LinkedHashSet<String>()

    // 1. Seed with the annotation-processor-generated entries (pure AP output).
    val apFile = annotationProcessorAdviceFile.orNull?.asFile
    if (apFile != null && apFile.exists()) {
      apFile
          .readLines(Charsets.UTF_8)
          .map { it.trim() }
          .filter { it.isNotEmpty() && !it.startsWith("#") }
          .forEach { entries.add(it) }
      logger.info("Loaded ${entries.size} entry/entries from annotation processor output")
    }

    // 2. Scan all dependency JARs for their META-INF/advice_annotations entries.
    var jarsWithAdvices = 0
    classpathJars.forEach { file ->
      if (!file.exists() || !file.name.endsWith(".jar")) return@forEach
      try {
        JarFile(file).use { jar ->
          val jarEntry = jar.getJarEntry(metaInfPath) ?: return@use
          logger.info("Found $fileName in ${file.name}")
          jar.getInputStream(jarEntry).use { inputStream ->
            inputStream
                .bufferedReader(Charsets.UTF_8)
                .readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .forEach { entries.add(it) }
          }
          jarsWithAdvices++
        }
      } catch (e: Exception) {
        logger.warn("Failed to scan JAR for advice entries: ${file.absolutePath}", e)
      }
    }

    logger.info(
        "Merging $jarsWithAdvices dependency advice file(s); total ${entries.size} unique entries")

    // 3. Write the fully merged, de-duplicated content to the staging output file.
    //    The file is always created so the @OutputFile is present after the task runs, even when
    //    there are no entries (the AdviceTransformerPlugin handles an empty file gracefully).
    val outputFile = mergedAdviceFile.get().asFile
    outputFile.parentFile.mkdirs()
    outputFile.writeText(
        if (entries.isEmpty()) ""
        else entries.joinToString(separator = System.lineSeparator(), postfix = System.lineSeparator()))
    logger.info("Merged advices written to: ${outputFile.absolutePath}")
  }
}

