/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import github.benslabbert.vdw.codegen.commons.eb.AddUserToContextServiceInterceptor;
import github.benslabbert.vdw.codegen.commons.eb.ProxyHandlerUtils;
import github.benslabbert.vdw.codegen.generator.ProcessorBase.GeneratedFile;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.AuthenticationInterceptor;
import io.vertx.serviceproxy.AuthorizationInterceptor;
import io.vertx.serviceproxy.ProxyHandler;
import io.vertx.serviceproxy.impl.InterceptorHolder;
import jakarta.annotation.Generated;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.lang.model.element.Element;

class VertxEBProxyHandlerGenerator {

  GeneratedFile generateTempFile(Element e) {
    EventBusService annotation = e.getAnnotation(EventBusService.class);
    String address = annotation.address();

    List<EBGeneratorUtil.ServiceMethod> methods = EBGeneratorUtil.getMethods(e);

    String canonicalName = e.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    String generatedClassName = e.getSimpleName() + "VertxEBProxyHandler";

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", classPackage);
      out.println();

      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.printf("import %s;%n", AddUserToContextServiceInterceptor.class.getCanonicalName());
      out.printf("import %s;%n", ProxyHandlerUtils.class.getCanonicalName());
      out.printf("import %s;%n", Future.class.getCanonicalName());
      out.printf("import %s;%n", Vertx.class.getCanonicalName());
      out.printf("import %s;%n", Message.class.getCanonicalName());
      out.printf("import %s;%n", JsonObject.class.getCanonicalName());
      out.printf("import %s;%n", AuthenticationInterceptor.class.getCanonicalName());
      out.printf("import %s;%n", AuthorizationInterceptor.class.getCanonicalName());
      out.printf("import %s;%n", ProxyHandler.class.getCanonicalName());
      out.printf("import %s;%n", InterceptorHolder.class.getCanonicalName());
      out.printf("import %s;%n", Inject.class.getCanonicalName());
      out.printf("import %s;%n", Provider.class.getCanonicalName());
      out.printf("import %s;%n", Singleton.class.getCanonicalName());
      out.printf("import %s;%n", List.class.getCanonicalName());
      for (EBGeneratorUtil.ServiceMethod m : methods) {
        out.printf("import %s;%n", m.paramTypeImport());
        out.printf("import %s;%n", m.returnTypeImport());
      }
      out.println();

      out.println("@Singleton");
      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.printf("public final class %s extends ProxyHandler {%n", generatedClassName);
      out.println();
      out.println(
          "\tprivate final Provider<AuthorizationInterceptor> authorizationInterceptorProvider;");
      out.println("\tprivate final AuthenticationInterceptor authenticationInterceptor;");
      out.printf("\tprivate final %s service;%n", e.getSimpleName());
      out.println("\tprivate final Vertx vertx;");
      out.println();

      out.println("\t@Inject");
      out.printf(
          "\t%s(Vertx vertx, %s service, AuthenticationInterceptor authenticationInterceptor,"
              + " Provider<AuthorizationInterceptor> authorizationInterceptorProvider) {%n",
          generatedClassName, e.getSimpleName());
      out.println("\t\tthis.vertx = vertx;");
      out.println("\t\tthis.service = service;");
      out.println("\t\tthis.authenticationInterceptor = authenticationInterceptor;");
      out.println("\t\tthis.authorizationInterceptorProvider = authorizationInterceptorProvider;");
      out.println("\t}");
      out.println();

      out.println("\tpublic void register() {");
      out.println("\t\tList<InterceptorHolder> interceptorHolders =");
      out.println("\t\t\tList.of(");
      out.println("\t\t\t\tnew InterceptorHolder(authenticationInterceptor),");

      for (EBGeneratorUtil.ServiceMethod m : methods) {
        if (null == m.role()) {
          continue;
        }
        out.printf(
            "\t\t\t\tProxyHandlerUtils.roleForAction(authorizationInterceptorProvider, \"%s\","
                + " \"%s\"),%n",
            m.methodName(), m.role());
      }
      out.println("\t\t\t\tAddUserToContextServiceInterceptor.create()");
      out.println("\t\t\t);");
      out.printf("\t\tthis.consumer = register(vertx, \"%s\", interceptorHolders);%n", address);
      out.println("\t}");
      out.println();

      out.println("\t@Override");
      out.println("\tpublic void handle(Message<JsonObject> msg) {");
      out.println("\t\tProxyHandlerUtils.handleMessage(msg, this::execute);");
      out.println("\t}");
      out.println();

      out.println("\tprivate Future<JsonObject> execute(String action, JsonObject request) {");
      out.println("\t\treturn switch (action) {");
      for (EBGeneratorUtil.ServiceMethod m : methods) {
        out.printf(
            "\t\t\tcase \"%s\" -> service.%s(%s.fromJson(request)).map(%s::toJson);%n",
            m.methodName(), m.methodName(), m.paramTypeName(), m.returnTypeName());
      }
      out.println(
          "\t\t\tcase null -> Future.failedFuture(new IllegalStateException(\"Action cannot be"
              + " null\"));");
      out.println(
          "\t\t\tdefault -> Future.failedFuture(new IllegalStateException(\"Unknown action: \" +"
              + " action));");
      out.println("\t\t};");
      out.println("\t}");
      out.println();

      out.println("}");
    }

    return new GeneratedFile(stringWriter, classPackage + "." + generatedClassName);
  }
}
