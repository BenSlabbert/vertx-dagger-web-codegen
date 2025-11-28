/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.txmanager.agent;

import github.benslabbert.vdw.codegen.annotation.transaction.Transactional.Propagation;
import github.benslabbert.vdw.codegen.txmanager.PlatformTransactionManager;
import net.bytebuddy.asm.Advice.FieldValue;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.Origin;
import org.slf4j.Logger;

final class RequiresExistingAdvice {

  private RequiresExistingAdvice() {}

  @OnMethodEnter
  static void onEnter(
      @Origin("#t\\##m") String methodName,
      @TransactionPropagation Propagation propagation,
      @FieldValue(value = "log") Logger log) {

    if (log.isDebugEnabled()) {
      log.debug("Entering advised method: {} with propagation: {}", methodName, propagation);
    }

    PlatformTransactionManager.ensureActive();

    log.debug("transaction active method: {}", methodName);
  }
}
