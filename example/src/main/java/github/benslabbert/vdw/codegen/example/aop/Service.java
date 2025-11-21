/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop;

import github.benslabbert.vdw.codegen.aop.AdviceExecutor;
import github.benslabbert.vdw.codegen.aop.LogEntry;
import github.benslabbert.vdw.codegen.aop.LogEntryAdvice;
import github.benslabbert.vdw.codegen.aop.Observed;
import github.benslabbert.vdw.codegen.aop.ObservedImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service {

  private static final Logger log = LoggerFactory.getLogger(Service.class);

  static {
    // this initialization should be in the applications mains Provider
    AdviceExecutor.addAdvice("github.benslabbert.vdw.codegen.aop.LogEntry", LogEntryAdvice::new);
    AdviceExecutor.addAdvice(
        "github.benslabbert.vdw.codegen.example.aop.CustomAdvice", CustomAdviceImpl::new);
    AdviceExecutor.addAdvice("github.benslabbert.vdw.codegen.aop.Observed", ObservedImpl::new);
  }

  static void main() {
    Service service = new Service();
    service.doWork();
    String param = service.doWork("param");
    Example example = new Example();
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
