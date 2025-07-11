/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import com.palantir.javapoet.ClassName;
import java.util.List;

record MethodRequest(
    String methodName,
    String httpMethod,
    String path,
    List<String> roles,
    String produces,
    String consumes,
    int responseCode,
    boolean isVoid,
    ClassName returnType,
    List<MethodParameter> parameters) {

  boolean hasRoles() {
    return null != roles && !roles.isEmpty();
  }
}
