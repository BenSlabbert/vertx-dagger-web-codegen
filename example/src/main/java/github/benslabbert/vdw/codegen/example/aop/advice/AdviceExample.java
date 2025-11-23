/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop.advice;

import github.benslabbert.vdw.codegen.aop.AdviceExecutor;
import github.benslabbert.vdw.codegen.aop.LogEntry;
import github.benslabbert.vdw.codegen.aop.LogEntryAdvice;
import github.benslabbert.vdw.codegen.aop.Observed;
import github.benslabbert.vdw.codegen.aop.ObservedImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdviceExample {

  private static final Logger log = LoggerFactory.getLogger(AdviceExample.class);

  static {
    // this initialization should be in the applications mains Provider
    AdviceExecutor.addAdvice("github.benslabbert.vdw.codegen.aop.LogEntry", LogEntryAdvice::new);
    AdviceExecutor.addAdvice(
        "github.benslabbert.vdw.codegen.example.aop.CustomAdvice", CustomAdviceImpl::new);
    AdviceExecutor.addAdvice("github.benslabbert.vdw.codegen.aop.Observed", ObservedImpl::new);
  }

  public static void main(String[] args) {
    AdviceExample service = new AdviceExample();
    service.doWork();
    String param = service.doWork("param");
    CustomAdviceExample example = new CustomAdviceExample();
    example.example();

    service.throwsException();
  }

  @LogEntry
  @CustomAdvice
  public void doWork() {
    log.info("do work");
  }

  @LogEntry
  @Observed
  public String doWork(String arg) {
    log.info("do work {}", arg);
    return arg;
  }

  @Observed
  public void throwsException() {
    log.info("throwsException");
    throw new RuntimeException();
  }
}
