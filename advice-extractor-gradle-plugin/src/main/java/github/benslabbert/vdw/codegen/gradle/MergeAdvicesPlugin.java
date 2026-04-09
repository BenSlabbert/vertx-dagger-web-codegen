/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.tasks.Jar;

/**
 * Gradle plugin that registers and auto-configures {@link MergeAdvicesTask} for projects that
 * apply the {@code java} plugin.
 *
 * <p>Apply this plugin with:
 *
 * <pre>{@code
 * plugins {
 *   id("github.benslabbert.vdw.codegen.merge-advices")
 * }
 * }</pre>
 *
 * <p>The plugin wires the following defaults automatically:
 *
 * <ul>
 *   <li>{@code annotationProcessorAdviceFile} → the AP-generated {@code
 *       META-INF/advice_annotations} file in {@code compileJava}'s output directory
 *   <li>{@code classpathJars} → the full {@code runtimeClasspath} configuration
 *   <li>{@code mergedAdviceFile} → {@code build/tmp/mergeAdvices/META-INF/advice_annotations}
 * </ul>
 *
 * <p>The {@code jar} task is configured to exclude the raw AP file and include the merged staging
 * file so that published JARs contain the fully-merged advice list.
 */
public class MergeAdvicesPlugin implements Plugin<Project> {

  /** The plugin identifier used in {@code plugins { id("...") }}. */
  public static final String PLUGIN_ID = "github.benslabbert.vdw.codegen.merge-advices";

  /** The default name of the advice annotation file inside {@code META-INF}. */
  public static final String DEFAULT_ADVICE_FILE_NAME = "advice_annotations";

  /** The name of the task registered by this plugin. */
  public static final String TASK_NAME = "mergeAdvices";

  @Override
  public void apply(Project project) {
    // React to the java plugin so the plugin is safe to apply before java is applied.
    project
        .getPlugins()
        .withType(
            JavaPlugin.class,
            javaPlugin -> {
              TaskProvider<JavaCompile> compileJava =
                  project.getTasks().named("compileJava", JavaCompile.class);

              TaskProvider<MergeAdvicesTask> mergeAdvicesTask =
                  project
                      .getTasks()
                      .register(
                          TASK_NAME,
                          MergeAdvicesTask.class,
                          task -> {
                            task.setDescription(
                                "Merges META-INF/advice_annotations from AP output and"
                                    + " dependency JARs.");
                            task.setGroup("build");

                            // Default advice file name convention.
                            task.getAdviceFileName().convention(DEFAULT_ADVICE_FILE_NAME);

                            // Wire annotationProcessorAdviceFile to compileJava's output.
                            task.getAnnotationProcessorAdviceFile()
                                .convention(
                                    compileJava.flatMap(
                                        jc ->
                                            jc.getDestinationDirectory()
                                                .file(
                                                    task.getAdviceFileName()
                                                        .map(name -> "META-INF/" + name))));

                            // Scan the full runtimeClasspath (superset of compileClasspath).
                            task.getClasspathJars()
                                .from(
                                    project
                                        .getConfigurations()
                                        .named(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME));

                            // Stage merged output under build/tmp/mergeAdvices/.
                            task.getMergedAdviceFile()
                                .convention(
                                    project
                                        .getLayout()
                                        .getBuildDirectory()
                                        .file(
                                            task.getAdviceFileName()
                                                .map(
                                                    name ->
                                                        "tmp/mergeAdvices/META-INF/" + name)));
                          });

              // Replace the raw AP file in the JAR with the fully merged staging file.
              project
                  .getTasks()
                  .named(
                      JavaPlugin.JAR_TASK_NAME,
                      Jar.class,
                      jar -> {
                        jar.dependsOn(mergeAdvicesTask);
                        jar.exclude("META-INF/" + DEFAULT_ADVICE_FILE_NAME);
                        jar.from(
                            mergeAdvicesTask.flatMap(MergeAdvicesTask::getMergedAdviceFile),
                            spec -> spec.into("META-INF"));
                      });
            });
  }
}
