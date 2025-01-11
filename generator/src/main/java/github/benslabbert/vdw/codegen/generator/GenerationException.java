/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

public class GenerationException extends RuntimeException {

  public GenerationException(String message) {
    super(message);
  }

  public GenerationException(Exception e) {
    super(e);
  }
}
