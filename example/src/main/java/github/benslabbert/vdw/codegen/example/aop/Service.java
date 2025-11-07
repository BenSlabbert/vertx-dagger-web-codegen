/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop;

import github.benslabbert.vdw.codegen.aop.BeforeAdviceExecutor;
import github.benslabbert.vdw.codegen.aop.LogEntry;
import github.benslabbert.vdw.codegen.aop.LogEntryAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service {
  private static final Logger log = LoggerFactory.getLogger(Service.class);

  static {
    // this initialization should be in the applications mains Provider
    BeforeAdviceExecutor.addBeforeAdvice(
        "github.benslabbert.vdw.codegen.aop.LogEntry", LogEntryAdvice::new);
    BeforeAdviceExecutor.addBeforeAdvice(
        "github.benslabbert.vdw.codegen.example.aop.CustomAdvice", CustomAdviceImpl::new);
  }

  @LogEntry
  @CustomAdvice
  public void doWork() {
    // added by byte-buddy
    BeforeAdviceExecutor.before(
        "github.benslabbert.vdw.codegen.aop.LogEntry",
        "github.benslabbert.vdw.codegen.aop.Service",
        "doWork");
    BeforeAdviceExecutor.before(
        "github.benslabbert.vdw.codegen.example.aop.CustomAdvice",
        "github.benslabbert.vdw.codegen.aop.Service",
        "doWork");
    // added by byte-buddy

    // in the byte-buddy Plugin, we can read resources to get a list of the custom advice to apply
    // we can use an annotation process to find all the advice annotations in the project and
    // generate the file!
    // and annotation class annotated with @BeforeAdvice(xxx.class)
    // we can generate a resource in META-INF/advices
    // it can contain all the canonical names of advice classes
    //      Class<? extends Annotation> aClass = Class.forName("");
    //
    //     InputStream resourceAsStream = getClass().getResourceAsStream("file.txt");
    //    System.err.println("Reading file ? " + (null == resourceAsStream));

    //   @Override
    //  public boolean matches(TypeDescription typeDefinitions) {
    //    ElementMatcher<MethodDescription> matcher =
    // isAnnotatedWith(named("com.example.MyAnnotation"));
    //    return typeDefinitions.getDeclaredMethods().stream()
    //            .anyMatch(matcher::matches);
    //  }

    // original method
    log.info("do work");
    // original method
  }
}
