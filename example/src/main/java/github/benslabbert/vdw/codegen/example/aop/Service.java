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

  public static void main(String[] args) {
    Service service = new Service();
    service.doWork();
    service.doWork("param");
    Example example = new Example();
    example.example();
  }

  @LogEntry
  @CustomAdvice
  public void doWork() {
    // original method
    log.info("do work");
    // original method
  }

  @LogEntry
  public void doWork(String arg) {
    // original method
    log.info("do work {}", arg);
    // original method
  }
}
