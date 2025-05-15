/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import github.benslabbert.vdw.codegen.commons.eb.EventBusServiceConfigurer;
import github.benslabbert.vdw.codegen.generator.ProcessorBase.GeneratedFile;
import io.vertx.serviceproxy.ProxyHandler;
import jakarta.annotation.Generated;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.lang.model.element.Element;

class EventBusServiceModuleBindings {

  GeneratedFile generateTempFile(Element e) {
    String canonicalName = e.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    String generatedClassName = e.getSimpleName() + "_EB_Module_Bindings";
    String generatedServiceConfigurerClassName =
        e.getSimpleName() + "_EventBusServiceConfigurerImpl";
    String generatedVertxEBProxyHandlerClassName = e.getSimpleName() + "VertxEBProxyHandler";

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", classPackage);
      out.println();

      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.printf("import %s;%n", Binds.class.getCanonicalName());
      out.printf("import %s;%n", Module.class.getCanonicalName());
      out.printf("import %s;%n", IntoSet.class.getCanonicalName());
      out.printf("import %s;%n", EventBusServiceConfigurer.class.getCanonicalName());
      out.printf("import %s;%n", ProxyHandler.class.getCanonicalName());
      out.println();

      out.println("@Module");
      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.printf("interface %s {%n", generatedClassName);
      out.println();
      out.println("\t@Binds");
      out.println("\t@IntoSet");
      out.printf(
          "\tEventBusServiceConfigurer bindEventBusServiceConfigurer(%s c);%n",
          generatedServiceConfigurerClassName);
      out.println();
      out.println("\t@Binds");
      out.println("\t@IntoSet");
      out.printf(
          "\tProxyHandler bindProxyHandler(%s ph);%n", generatedVertxEBProxyHandlerClassName);
      out.println("}");
    }

    return new GeneratedFile(stringWriter, classPackage + "." + generatedClassName);
  }
}
