/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.txmanager.agent;

import github.benslabbert.vdw.codegen.annotation.transaction.AfterCommit;
import github.benslabbert.vdw.codegen.annotation.transaction.BeforeCommit;
import github.benslabbert.vdw.codegen.annotation.transaction.Transactional;
import net.bytebuddy.description.annotation.AnnotationDescription.Loadable;

class AnnotationDeclarationException extends RuntimeException {

  AnnotationDeclarationException(
      Loadable<Transactional> transactional,
      Loadable<BeforeCommit> beforeCommit,
      Loadable<AfterCommit> afterCommit) {
    super(
        "cannot have more than one annotations: transactional="
            + transactional
            + ", beforeCommit="
            + beforeCommit
            + ", afterCommit="
            + afterCommit);
  }
}
