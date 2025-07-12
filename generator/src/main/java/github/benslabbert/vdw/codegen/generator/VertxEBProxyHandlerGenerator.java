/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.EventBusService;
import github.benslabbert.vdw.codegen.annotation.NoAuthCheck;
import github.benslabbert.vdw.codegen.commons.ValidatorProvider;
import github.benslabbert.vdw.codegen.commons.eb.AddUserToContextServiceInterceptor;
import github.benslabbert.vdw.codegen.commons.eb.ProxyHandlerUtils;
import github.benslabbert.vdw.codegen.generator.ProcessorBase.GeneratedFile;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.OutputUnit;
import io.vertx.serviceproxy.AuthenticationInterceptor;
import io.vertx.serviceproxy.AuthorizationInterceptor;
import io.vertx.serviceproxy.ProxyHandler;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;
import io.vertx.serviceproxy.impl.InterceptorHolder;
import jakarta.annotation.Generated;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class VertxEBProxyHandlerGenerator {

  GeneratedFile generateTempFile(Element e) {
    EventBusService annotation = e.getAnnotation(EventBusService.class);
    String address = annotation.address();
    NoAuthCheck noAuthCheck = e.getAnnotation(NoAuthCheck.class);
    boolean enableAuth = null == noAuthCheck;

    List<EBGeneratorUtil.ServiceMethod> methods = EBGeneratorUtil.getMethods(e);

    if (!enableAuth) {
      boolean anyMatch =
          methods.stream()
              .map(EBGeneratorUtil.ServiceMethod::roles)
              .filter(Objects::nonNull)
              .anyMatch(roles -> !roles.isEmpty());
      if (anyMatch) {
        throw new GenerationException(
            "cannot use @NoAuthCheck and @Roles annotations on the same handler");
      }
    }

    boolean noRolesSpecified =
        methods.stream()
            .map(EBGeneratorUtil.ServiceMethod::roles)
            .anyMatch(r -> null == r || r.isEmpty());
    if (noRolesSpecified) {
      enableAuth = false;
    }

    boolean useValidator = methods.stream().anyMatch(EBGeneratorUtil.ServiceMethod::validated);

    String canonicalName = e.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    String generatedClassName = e.getSimpleName() + "VertxEBProxyHandler";

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", classPackage);
      out.println();

      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.printf("import %s;%n", Valid.class.getCanonicalName());
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
      out.printf("import %s;%n", ServiceException.class.getCanonicalName());
      out.printf("import %s;%n", ServiceExceptionMessageCodec.class.getCanonicalName());
      out.printf("import %s;%n", ConstraintViolation.class.getCanonicalName());
      out.printf("import %s;%n", Set.class.getCanonicalName());
      out.printf("import %s;%n", ValidatorProvider.class.getCanonicalName());
      out.printf("import %s;%n", Validator.class.getCanonicalName());
      out.printf("import %s;%n", OutputUnit.class.getCanonicalName());
      out.printf("import %s;%n", Logger.class.getCanonicalName());
      out.printf("import %s;%n", LoggerFactory.class.getCanonicalName());
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
      out.printf(
          "private static final Logger log = LoggerFactory.getLogger(%s.class);%n",
          generatedClassName);
      out.println();
      if (enableAuth) {
        out.println(
            "private final Provider<AuthorizationInterceptor> authorizationInterceptorProvider;");
        out.println("private final AuthenticationInterceptor authenticationInterceptor;");
      }
      if (useValidator) {
        out.println("private final ValidatorProvider validatorProvider;");
      }
      out.printf("private final %s service;%n", e.getSimpleName());
      out.println("private final Vertx vertx;");
      out.println();

      out.println("@Inject");

      if (enableAuth && useValidator) {
        out.printf(
            """
            %s(Vertx vertx,
            %s service,
            ValidatorProvider validatorProvider,
            AuthenticationInterceptor authenticationInterceptor,
            Provider<AuthorizationInterceptor> authorizationInterceptorProvider) {
            """,
            generatedClassName, e.getSimpleName());
      }

      if (enableAuth && !useValidator) {
        out.printf(
            """
            %s(Vertx vertx,
            %s service,
            AuthenticationInterceptor authenticationInterceptor,
            Provider<AuthorizationInterceptor> authorizationInterceptorProvider) {
            """,
            generatedClassName, e.getSimpleName());
      }

      if (!enableAuth && useValidator) {
        out.printf(
            """
            %s(Vertx vertx,
            %s service,
            ValidatorProvider validatorProvider) {
            """,
            generatedClassName, e.getSimpleName());
      }

      if (!enableAuth && !useValidator) {
        out.printf("%s(Vertx vertx, %s service) {", generatedClassName, e.getSimpleName());
      }

      out.println("this.vertx = vertx;");
      out.println("this.service = service;");
      if (useValidator) {
        out.println("this.validatorProvider = validatorProvider;");
      }
      if (enableAuth) {
        out.println("this.authenticationInterceptor = authenticationInterceptor;");
        out.println("this.authorizationInterceptorProvider = authorizationInterceptorProvider;");
      }
      out.println("try {");
      out.println(
          "this.vertx.eventBus().registerDefaultCodec(ServiceException.class, new"
              + " ServiceExceptionMessageCodec());");
      out.println("} catch (IllegalStateException ex) { /* ignore */ }");
      out.println("}");
      out.println();

      out.println("public void register() {");
      out.println("List<InterceptorHolder> interceptorHolders =");
      out.println("List.of(");
      if (enableAuth) {
        out.println("new InterceptorHolder(authenticationInterceptor),");
      }

      for (EBGeneratorUtil.ServiceMethod m : methods) {
        if (null == m.roles()) {
          continue;
        }
        if (1 == m.roles().size()) {
          out.printf(
              "ProxyHandlerUtils.roleForAction(authorizationInterceptorProvider, \"%s\","
                  + " \"%s\"),%n",
              m.methodName(), m.roles().getFirst());
        } else {
          String roles =
              m.roles().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "));
          out.printf(
              "ProxyHandlerUtils.rolesForAction(authorizationInterceptorProvider, \"%s\",%s),%n",
              m.methodName(), roles);
        }
      }
      if (enableAuth) {
        out.println("AddUserToContextServiceInterceptor.create()");
      }
      out.println(");");
      out.printf("register(vertx, \"%s\", interceptorHolders)%n", address);
      out.println(".endHandler(ignore -> log.info(\"stream ended\"))");
      out.println(".exceptionHandler(err -> log.error(\"stream error\", err));");
      out.println("}");
      out.println();

      out.println("@Override");
      out.println("public void handle(Message<JsonObject> msg) {");
      out.println("ProxyHandlerUtils.handleMessage(msg, this::execute);");
      out.println("}");
      out.println();

      out.println("private Future<JsonObject> execute(String action, JsonObject request) {");
      out.println("return switch (action) {");
      for (EBGeneratorUtil.ServiceMethod m : methods) {
        out.printf(
            """
case "%s" -> {
    io.vertx.json.schema.Validator validator = %s.getValidator();
    OutputUnit outputUnit = validator.validate(request);
    if (!outputUnit.getValid()) {
        yield Future.failedFuture(new ServiceException(400, outputUnit.toString(), outputUnit.toJson()));
    }
""",
            m.methodName(), m.paramTypeName());

        if (m.validated()) {
          out.printf(
              """
        %s r = %s.fromJson(request);
        Set<ConstraintViolation<%s>> violations = validatorProvider.getValidator().validate(r);
        if (!violations.isEmpty()) {
            JsonObject json = getErrorJson(violations);
            yield Future.failedFuture(new ServiceException(400, json.encode(), json));
        }

        yield service.%s(r).map(%s::toJson);
""",
              m.paramTypeName(),
              m.paramTypeName(),
              m.paramTypeName(),
              m.methodName(),
              m.returnTypeName());
        } else {
          out.printf(
              "yield service.%s(%s.fromJson(request)).map(%s::toJson);%n",
              m.methodName(), m.paramTypeName(), m.returnTypeName());
        }
        out.println("}");
      }
      out.println(
          "case null -> Future.failedFuture(new IllegalStateException(\"Action cannot be"
              + " null\"));");
      out.println(
          "default -> Future.failedFuture(new IllegalStateException(\"Unknown action: \" +"
              + " action));");
      out.println("};");
      out.println("}");
      out.println();

      out.println(
          """
    private static <T> JsonObject getErrorJson(Set<ConstraintViolation<T>> violations) {
        List<JsonObject> errors =
            violations.stream()
                .map(v -> new JsonObject().put("field", v.getPropertyPath().toString()).put("message", v.getMessage()))
                .toList();
        return new JsonObject().put("errors", errors);
    }
""");
      out.println("}");
    }

    return new GeneratedFile(stringWriter, classPackage + "." + generatedClassName);
  }
}
