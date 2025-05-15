/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class VertxEBProxyGenerators extends ProcessorBase {

  public VertxEBProxyGenerators() {
    super(Set.of(EventBusService.class.getCanonicalName()));
  }

  @Override
  List<GeneratedFile> generateTempFile(Element e) {
    if (ElementKind.INTERFACE != e.getKind()) {
      throw new GenerationException("@EventBusService can only be applied to interfaces");
    }

    Element enclosingElement = e.getEnclosingElement();

    if (ElementKind.PACKAGE != enclosingElement.getKind()) {
      throw new GenerationException("@EventBusService can only be applied to top level interfaces");
    }

    var vertxEBProxyHandlerGenerator = new VertxEBProxyHandlerGenerator();
    var vertxEBClientProxyGenerator = new VertxEBClientProxyGenerator();
    var eventBusServiceConfigurerGenerator = new EventBusServiceConfigurerGenerator();
    var eventBusServiceModuleBindings = new EventBusServiceModuleBindings();

    return List.of(
        vertxEBProxyHandlerGenerator.generateTempFile(e),
        vertxEBClientProxyGenerator.generateTempFile(e),
        eventBusServiceConfigurerGenerator.generateTempFile(e),
        eventBusServiceModuleBindings.generateTempFile(e));
  }
}
