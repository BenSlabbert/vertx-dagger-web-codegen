/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.commons;

import jakarta.validation.Validator;

public interface ValidatorProvider {

  Validator getValidator();
}
