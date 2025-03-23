/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import github.benslabbert.vdw.codegen.commons.eb.ClientProxyUtils;
import github.benslabbert.vdw.codegen.generator.EBGeneratorUtil.ServiceMethod;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;
import jakarta.annotation.Generated;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.lang.model.element.Element;

class VertxEBClientProxyGenerator {

  ProcessorBase.GeneratedFile generateTempFile(Element e) throws Exception {
    EventBusService annotation = e.getAnnotation(EventBusService.class);
    String address = annotation.address();

    List<ServiceMethod> methods = EBGeneratorUtil.getMethods(e);

    String canonicalName = e.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    String generatedClassName = e.getSimpleName() + "VertxEBClientProxy";

    Path tempFile = Files.createTempFile(generatedClassName + "-", ".java");

    try (PrintWriter out =
        new PrintWriter(Files.newBufferedWriter(tempFile, StandardOpenOption.WRITE))) {
      out.printf("package %s;%n", classPackage);
      out.println();

      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.printf("import %s;%n", ClientProxyUtils.class.getCanonicalName());
      out.printf("import %s;%n", Future.class.getCanonicalName());
      out.printf("import %s;%n", Vertx.class.getCanonicalName());
      out.printf("import %s;%n", DeliveryOptions.class.getCanonicalName());
      out.printf("import %s;%n", ServiceException.class.getCanonicalName());
      out.printf("import %s;%n", ServiceExceptionMessageCodec.class.getCanonicalName());
      for (EBGeneratorUtil.ServiceMethod m : methods) {
        out.printf("import %s;%n", m.paramTypeImport());
        out.printf("import %s;%n", m.returnTypeImport());
      }
      out.println();

      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.printf("public final class %s implements %s {%n", generatedClassName, e.getSimpleName());
      out.println();
      out.println("\tprivate final DeliveryOptions opts;");
      out.println("\tprivate final Vertx vertx;");
      out.println();

      out.printf("\tpublic %s(Vertx vertx) {%n", generatedClassName);
      out.println("\t\tthis(vertx, new DeliveryOptions());");
      out.println("\t}");
      out.println();

      out.printf("\tpublic %s(Vertx vertx, DeliveryOptions options) {%n", generatedClassName);
      out.println("\t\tthis.vertx = vertx;");
      out.println("\t\tthis.opts = options;");
      out.println("\t\ttry {");
      out.println(
          "\t\t\tthis.vertx.eventBus().registerDefaultCodec(ServiceException.class, new"
              + " ServiceExceptionMessageCodec());");
      out.println("\t\t} catch (IllegalStateException ex) { /* ignore */ }");
      out.println("\t}");
      out.println();

      for (ServiceMethod sm : methods) {
        out.println("\t@Override");
        out.printf(
            "\tpublic Future<%s> %s(%s req) {%n",
            sm.returnTypeName(), sm.methodName(), sm.paramTypeName());
        out.printf(
            "\t\treturn ClientProxyUtils.getResponseFuture(vertx, \"%s\", opts, req.toJson(),"
                + " \"%s\", %s::fromJson);%n",
            address, sm.methodName(), sm.returnTypeName());
        out.println("\t}");
        out.println();
      }

      out.println("}");
    }

    return new ProcessorBase.GeneratedFile(tempFile, classPackage + "." + generatedClassName);
  }
}
