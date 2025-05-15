/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.commons.eb.EventBusServiceConfigurer;
import github.benslabbert.vdw.codegen.generator.ProcessorBase.GeneratedFile;
import jakarta.annotation.Generated;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.lang.model.element.Element;

class EventBusServiceConfigurerGenerator {

  GeneratedFile generateTempFile(Element e) {
    String canonicalName = e.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    String generatedClassName = e.getSimpleName() + "_EventBusServiceConfigurerImpl";
    String proxyName = e.getSimpleName() + "VertxEBProxyHandler";

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", classPackage);
      out.println();

      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.printf("import %s;%n", EventBusServiceConfigurer.class.getCanonicalName());
      out.printf("import %s;%n", Inject.class.getCanonicalName());
      out.printf("import %s;%n", Singleton.class.getCanonicalName());
      out.printf("import %s.%s;%n", classPackage, proxyName);
      out.println();

      out.println("@Singleton");
      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.printf("final class %s implements EventBusServiceConfigurer {%n", generatedClassName);
      out.println();

      out.printf("\tprivate final %s proxyHandler;%n", proxyName);
      out.println();

      out.println("\t@Inject");
      out.printf("\t%s(%s proxyHandler) {%n", generatedClassName, proxyName);
      out.println("\t\tthis.proxyHandler = proxyHandler;");
      out.println("\t}");
      out.println();

      out.println("\t@Override");
      out.println("\t\tpublic void configure() {");
      out.println("\t\t\tproxyHandler.register();");
      out.println("\t}");
      out.println();

      out.println("}");
    }

    return new GeneratedFile(stringWriter, classPackage + "." + generatedClassName);
  }
}
