/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.config;

import github.benslabbert.vdw.codegen.commons.ValidatorProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Singleton
final class ValidatorFactoryProvider implements ValidatorProvider, AutoCloseable {

  private final ValidatorFactory factory;

  @Inject
  ValidatorFactoryProvider() {
    this.factory = Validation.buildDefaultValidatorFactory();
  }

  @Override
  public void close() {
    if (factory != null) {
      factory.close();
    }
  }

  @Override
  public Validator getValidator() {
    return factory.getValidator();
  }
}
