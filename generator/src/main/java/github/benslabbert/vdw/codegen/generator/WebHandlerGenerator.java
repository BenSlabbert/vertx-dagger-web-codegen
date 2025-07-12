/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.TypeName;
import github.benslabbert.vdw.codegen.annotation.HasRole;
import github.benslabbert.vdw.codegen.annotation.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.RequiresModuleGeneration;
import github.benslabbert.vdw.codegen.annotation.WebHandler;
import github.benslabbert.vdw.codegen.annotation.WebRequest.All;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Connect;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Consumes;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Delete;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Head;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Headers;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Options;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Patch;
import github.benslabbert.vdw.codegen.annotation.WebRequest.PathParams;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Produces;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Put;
import github.benslabbert.vdw.codegen.annotation.WebRequest.QueryParams;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Request;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Response;
import github.benslabbert.vdw.codegen.annotation.WebRequest.RoutingContext;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Session;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Trace;
import github.benslabbert.vdw.codegen.annotation.WebRequest.User;
import github.benslabbert.vdw.codegen.annotation.WebRequest.UserContext;
import github.benslabbert.vdw.codegen.commons.ContextDataKey;
import github.benslabbert.vdw.codegen.commons.RequireRequestBodyHandler;
import github.benslabbert.vdw.codegen.commons.ResponseWriterUtil;
import github.benslabbert.vdw.codegen.commons.RoleAuthorizationHandlerProvider;
import github.benslabbert.vdw.codegen.commons.RouterConfigurer;
import github.benslabbert.vdw.codegen.commons.ValidateRequestBodyHandler;
import github.benslabbert.vdw.codegen.commons.ValidatorProvider;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import jakarta.annotation.Generated;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebHandlerGenerator extends ProcessorBase {

  public WebHandlerGenerator() {
    super(Set.of(WebHandler.class.getCanonicalName()));
  }

  @Override
  List<GeneratedFile> generateTempFile(Element element) {
    WebHandler webHandler = element.getAnnotation(WebHandler.class);
    NoAuthCheck noAuthCheck = element.getAnnotation(NoAuthCheck.class);
    boolean enableAuth = null == noAuthCheck;
    String path = webHandler.path();
    printNote("processing WebHandler", element);

    List<MethodRequest> annotatedMethods =
        element.getEnclosedElements().stream()
            .filter(f -> f.getModifiers().isEmpty() || f.getModifiers().contains(Modifier.PUBLIC))
            .filter(
                f ->
                    null != f.getAnnotation(All.class)
                        || null != f.getAnnotation(Get.class)
                        || null != f.getAnnotation(Post.class)
                        || null != f.getAnnotation(Put.class)
                        || null != f.getAnnotation(Patch.class)
                        || null != f.getAnnotation(Delete.class)
                        || null != f.getAnnotation(Head.class)
                        || null != f.getAnnotation(Trace.class)
                        || null != f.getAnnotation(Options.class)
                        || null != f.getAnnotation(Connect.class))
            .map(e -> buildMethodRequest(e, path))
            .toList();
    printNote(
        "found (%d) methods with WebRequest annotations".formatted(annotatedMethods.size()),
        element);
    annotatedMethods.forEach(am -> printNote("am: " + am, element));

    if (!enableAuth) {
      boolean anyMatch = annotatedMethods.stream().anyMatch(MethodRequest::hasRoles);
      if (anyMatch) {
        throw new GenerationException(
            "cannot use @NoAuthCheck and @Roles annotations on the same handler");
      }
    }

    boolean noRolesSpecified =
        annotatedMethods.stream().allMatch(m -> null == m.roles() || m.roles().isEmpty());
    if (noRolesSpecified) {
      enableAuth = false;
    }

    boolean useValidator =
        annotatedMethods.stream()
            .map(am -> requestBodyType(am.parameters()))
            .filter(Objects::nonNull)
            .anyMatch(MethodParameter::validateBody);

    String canonicalName = element.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    Name handlerName = element.getSimpleName();
    String generatedClassName = handlerName + "_Router";
    String ctxVariable = "_c";
    String handlerVariable = "_h";
    String respVariable = "_r";
    String validationProviderVariable = "_vp";
    String authProviderVariable = "_a";

    printNote("generated class: " + generatedClassName, element);

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", classPackage);
      out.println();

      getImports(annotatedMethods).forEach(anImport -> out.printf("import %s;%n", anImport));
      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.printf("import %s;%n", RequiresModuleGeneration.class.getCanonicalName());
      out.printf("import %s;%n", Inject.class.getCanonicalName());
      out.printf("import %s;%n", Singleton.class.getCanonicalName());
      out.printf("import %s;%n", HttpHeaderValues.class.getCanonicalName());
      out.printf("import %s;%n", HttpHeaders.class.getCanonicalName());
      out.printf("import %s;%n", JsonObject.class.getCanonicalName());
      out.printf("import %s;%n", Buffer.class.getCanonicalName());
      out.printf("import %s;%n", Router.class.getCanonicalName());
      out.printf("import %s;%n", io.vertx.ext.web.RoutingContext.class.getCanonicalName());
      out.printf("import %s;%n", HttpServerRequest.class.getCanonicalName());
      out.printf("import %s;%n", HttpServerResponse.class.getCanonicalName());
      out.printf("import %s;%n", io.vertx.ext.web.UserContext.class.getCanonicalName());
      out.printf("import %s;%n", io.vertx.ext.auth.User.class.getCanonicalName());
      out.printf("import %s;%n", io.vertx.ext.web.Session.class.getCanonicalName());
      out.printf("import %s;%n", Logger.class.getCanonicalName());
      out.printf("import %s;%n", LoggerFactory.class.getCanonicalName());
      out.printf("import %s;%n", ResponseWriterUtil.class.getCanonicalName());
      out.printf("import %s;%n", RouterConfigurer.class.getCanonicalName());
      out.printf("import %s;%n", ValidatorProvider.class.getCanonicalName());
      out.printf("import %s;%n", RoleAuthorizationHandlerProvider.class.getCanonicalName());
      out.printf("import %s;%n", Validator.class.getCanonicalName());
      out.printf("import %s;%n", ConstraintViolation.class.getCanonicalName());
      out.printf("import %s;%n", Set.class.getCanonicalName());
      out.printf("import %s;%n", Map.class.getCanonicalName());
      out.printf("import %s;%n", RequireRequestBodyHandler.class.getCanonicalName());
      out.printf("import %s;%n", ValidateRequestBodyHandler.class.getCanonicalName());
      out.printf("import %s;%n", ContextDataKey.class.getCanonicalName());
      out.println();

      out.println("@RequiresModuleGeneration");
      out.println("@Singleton");
      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.println("final class " + generatedClassName + " implements RouterConfigurer {");
      out.println();
      out.printf(
          "\tprivate static final Logger log = LoggerFactory.getLogger(%s.class);%n",
          generatedClassName);
      out.println();

      if (enableAuth) {
        out.printf("\tprivate final RoleAuthorizationHandlerProvider %s;%n", authProviderVariable);
      }

      if (useValidator) {
        out.printf("\tprivate final ValidatorProvider %s;%n", validationProviderVariable);
      }

      out.printf("\tprivate final %s %s;%n", handlerName, handlerVariable);
      out.println();
      out.println("\t@Inject");

      if (enableAuth && useValidator) {
        out.printf(
            "\t%s(RoleAuthorizationHandlerProvider %s, ValidatorProvider %s, %s %s) {%n",
            generatedClassName,
            authProviderVariable,
            validationProviderVariable,
            handlerName,
            handlerVariable);
      }

      if (!enableAuth && useValidator) {
        out.printf(
            "\t%s(ValidatorProvider %s, %s %s) {%n",
            generatedClassName, validationProviderVariable, handlerName, handlerVariable);
      }

      if (enableAuth && !useValidator) {
        out.printf(
            "\t%s(RoleAuthorizationHandlerProvider %s, %s %s) {%n",
            generatedClassName, authProviderVariable, handlerName, handlerVariable);
      }

      if (!enableAuth && !useValidator) {
        out.printf("\t%s(%s %s) {%n", generatedClassName, handlerName, handlerVariable);
      }

      if (enableAuth) {
        out.printf("\t\tthis.%s = %s;%n", authProviderVariable, authProviderVariable);
      }

      if (useValidator) {
        out.printf("\t\tthis.%s = %s;%n", validationProviderVariable, validationProviderVariable);
      }
      out.printf("\t\tthis.%s = %s;%n", handlerVariable, handlerVariable);
      out.println("\t}");
      out.println();
      out.println("\t@Override");
      out.println("\tpublic void route(Router router) {");
      for (var am : annotatedMethods) {
        out.printf("\t\tlog.info(\"%s %s\");%n", am.httpMethod(), am.path());
        out.printf("\t\trouter%n");

        switch (am.httpMethod()) {
          case All.METHOD -> out.printf("\t\t\t.route(\"%s\")%n", am.path());
          case Get.METHOD -> out.printf("\t\t\t.get(\"%s\")%n", am.path());
          case Post.METHOD -> out.printf("\t\t\t.post(\"%s\")%n", am.path());
          case Put.METHOD -> out.printf("\t\t\t.put(\"%s\")%n", am.path());
          case Patch.METHOD -> out.printf("\t\t\t.patch(\"%s\")%n", am.path());
          case Delete.METHOD -> out.printf("\t\t\t.delete(\"%s\")%n", am.path());
          case Trace.METHOD -> out.printf("\t\t\t.trace(\"%s\")%n", am.path());
          case Head.METHOD -> out.printf("\t\t\t.head(\"%s\")%n", am.path());
          case Options.METHOD -> out.printf("\t\t\t.options(\"%s\")%n", am.path());
          case Connect.METHOD -> out.printf("\t\t\t.connect(\"%s\")%n", am.path());
          default -> throw new GenerationException("unsupported HTTP method: " + am.httpMethod());
        }

        if (null != am.produces()) {
          out.printf("\t\t\t.produces(\"%s\")%n", am.produces());
        }
        if (null != am.consumes()) {
          out.printf("\t\t\t.consumes(\"%s\")%n", am.consumes());
        }

        if (null != am.roles()) {
          if (1 == am.roles().size()) {
            out.printf(
                "\t\t\t.handler(%s.forRole(\"%s\"))%n",
                authProviderVariable, am.roles().getFirst());
          } else {
            String roles =
                am.roles().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "));
            out.printf("\t\t\t.handler(%s.forRoles(%s))%n", authProviderVariable, roles);
          }
        }

        MethodParameter bodyParam = requestBodyType(am.parameters());
        if (null != bodyParam) {
          out.printf("\t\t\t.handler((RoutingContext %s) -> {%n", ctxVariable);
          out.printf(
              "\t\t\t\tRequireRequestBodyHandler.requireJsonBody(%s, %s.getValidator(), \"%s\");%n",
              ctxVariable, bodyParam.type().simpleName(), am.path());
          out.printf("\t\t\t})%n");

          if (bodyParam.validateBody()) {
            out.printf("\t\t\t.handler((RoutingContext %s) -> {%n", ctxVariable);
            out.printf(
                "\t\t\t\tValidateRequestBodyHandler.validateRequestBody(%s, %s.getValidator(),"
                    + " %s::fromJson);%n",
                ctxVariable, validationProviderVariable, bodyParam.type().simpleName());
            out.printf("\t\t\t})%n");
          } else {
            out.printf("\t\t\t.handler((RoutingContext %s) -> {%n", ctxVariable);
            out.printf(
                "\t\t\t\t%s.put(ContextDataKey.REQUEST_DATA,"
                    + " %s.fromJson(%s.get(ContextDataKey.REQUEST_JSON)));%n",
                ctxVariable, bodyParam.type().simpleName(), ctxVariable);
            out.printf("\t\t\t\t%s.next();%n", ctxVariable);
            out.printf("\t\t\t})%n");
          }
        }

        out.printf("\t\t\t.handler((RoutingContext %s) -> {%n", ctxVariable);
        fillHandlerCall(out, am, ctxVariable, handlerVariable, respVariable);
        out.printf("\t\t\t\t});%n");
        out.println();
      }
      out.println("\t}");
      out.println("}");
      out.println();
    }

    return List.of(new GeneratedFile(stringWriter, classPackage + "." + generatedClassName));
  }

  private static void fillHandlerCall(
      PrintWriter out,
      MethodRequest request,
      String ctxVariable,
      String handlerVariable,
      String respVariable) {

    if (request.isVoid()) {
      out.printf("\t\t\t\t\t%s.%s(", handlerVariable, request.methodName());
      MethodParams methodParams = getMethodParams(request, ctxVariable);
      out.print(methodParams.params());
      out.println(");");

      if (methodParams.routingContextUsed() || methodParams.responseUsed()) {
        return;
      }
      out.printf(
          "\t\t\t\t\tResponseWriterUtil.sendEmpty(%s, %d);%n", ctxVariable, request.responseCode());
      return;
    }

    boolean isStringReturn = false;
    boolean isBufferReturn = false;
    if (request.returnType().canonicalName().startsWith("java.lang.")) {
      if (!"String".equals(request.returnType().simpleName())) {
        throw new GenerationException(
            "unsupported return type: " + request.returnType().simpleName());
      }
      isStringReturn = true;
    }
    if ("io.vertx.core.buffer.Buffer".equals(request.returnType().canonicalName())) {
      isBufferReturn = true;
    }

    out.printf(
        "\t\t\t\t\t%s %s = %s.%s(",
        request.returnType().simpleName(), respVariable, handlerVariable, request.methodName());
    MethodParams methodParams = getMethodParams(request, ctxVariable);
    if (methodParams.routingContextUsed() || methodParams.responseUsed()) {
      throw new GenerationException(
          "cannot consume RoutingContext and specify a return type other than void");
    }
    out.print(methodParams.params());
    out.println(");");

    if (isStringReturn) {
      out.printf(
          "\t\t\t\t\tResponseWriterUtil.sendString(%s, %d, %s);%n",
          ctxVariable, request.responseCode(), respVariable);
    } else if (isBufferReturn) {
      out.printf(
          "\t\t\t\t\tResponseWriterUtil.sendBuffer(%s, %d, %s);%n",
          ctxVariable, request.responseCode(), respVariable);
    } else {
      out.printf(
          "\t\t\t\t\tResponseWriterUtil.sendJson(%s, %d, %s.toJson());%n",
          ctxVariable, request.responseCode(), respVariable);
    }
  }

  private static MethodParameter requestBodyType(List<MethodParameter> parameters) {
    for (var p : parameters) {
      if (null != p.body()) {
        return p;
      }
    }
    return null;
  }

  private record MethodParams(String params, boolean routingContextUsed, boolean responseUsed) {}

  private static MethodParams getMethodParams(MethodRequest request, String ctxVariable) {
    AtomicBoolean rcUsed = new AtomicBoolean(false);
    AtomicBoolean responseUsed = new AtomicBoolean(false);
    String params =
        request.parameters().stream()
            .map(
                p -> {
                  if (null != p.queryParams()) {
                    return "%s.queryParams()".formatted(ctxVariable);
                  }
                  if (null != p.pathParams()) {
                    return "%s.pathParams()".formatted(ctxVariable);
                  }
                  if (null != p.headers()) {
                    return "%s.request().headers()".formatted(ctxVariable);
                  }
                  if (null != p.session()) {
                    return "%s.session()".formatted(ctxVariable);
                  }
                  if (null != p.request()) {
                    return "%s.request()".formatted(ctxVariable);
                  }
                  if (null != p.response()) {
                    responseUsed.getAndSet(true);
                    return "%s.response()".formatted(ctxVariable);
                  }
                  if (null != p.userContext()) {
                    return "%s.userContext()".formatted(ctxVariable);
                  }
                  if (null != p.user()) {
                    return "%s.user()".formatted(ctxVariable);
                  }
                  if (null != p.routingContext()) {
                    rcUsed.getAndSet(true);
                    return ctxVariable;
                  }

                  return "(%s) %s.get(ContextDataKey.REQUEST_DATA)"
                      .formatted(p.type().simpleName(), ctxVariable);
                })
            .collect(Collectors.joining(", "));
    return new MethodParams(params, rcUsed.get(), responseUsed.get());
  }

  private static Set<String> getImports(List<MethodRequest> annotatedMethods) {
    Set<String> imports = new HashSet<>();

    for (var am : annotatedMethods) {
      for (var p : am.parameters()) {
        imports.add(p.type().canonicalName());
      }
      if (!am.isVoid()) {
        imports.add(am.returnType().canonicalName());
      }
    }

    return imports.stream().filter(f -> !f.contains("java.lang.")).collect(Collectors.toSet());
  }

  private record RequestMethodDetails(String path, String method, int responseCode) {

    RequestMethodDetails {
      path = PathSanitizer.sanitize(path);
    }
  }

  private RequestMethodDetails getRequestMethodDetails(Element e) {
    All all = e.getAnnotation(All.class);
    if (null != all) {
      return new RequestMethodDetails(all.path(), All.METHOD, all.responseCode());
    }
    Get get = e.getAnnotation(Get.class);
    if (null != get) {
      return new RequestMethodDetails(get.path(), Get.METHOD, get.responseCode());
    }
    Post post = e.getAnnotation(Post.class);
    if (null != post) {
      return new RequestMethodDetails(post.path(), Post.METHOD, post.responseCode());
    }
    Put put = e.getAnnotation(Put.class);
    if (null != put) {
      return new RequestMethodDetails(put.path(), Put.METHOD, put.responseCode());
    }
    Patch patch = e.getAnnotation(Patch.class);
    if (null != patch) {
      return new RequestMethodDetails(patch.path(), Patch.METHOD, patch.responseCode());
    }
    Delete delete = e.getAnnotation(Delete.class);
    if (null != delete) {
      return new RequestMethodDetails(delete.path(), Delete.METHOD, delete.responseCode());
    }
    Head head = e.getAnnotation(Head.class);
    if (null != head) {
      return new RequestMethodDetails(head.path(), Head.METHOD, head.responseCode());
    }
    Trace trace = e.getAnnotation(Trace.class);
    if (null != trace) {
      return new RequestMethodDetails(trace.path(), Trace.METHOD, trace.responseCode());
    }
    Options options = e.getAnnotation(Options.class);
    if (null != options) {
      return new RequestMethodDetails(options.path(), Options.METHOD, options.responseCode());
    }
    Connect connect = e.getAnnotation(Connect.class);
    if (null != connect) {
      return new RequestMethodDetails(connect.path(), Connect.METHOD, connect.responseCode());
    }

    printError("no WebRequest method for element", e);
    throw new GenerationException("no WebRequest method for element");
  }

  private MethodRequest buildMethodRequest(Element e, String basePath) {
    HasRole hasRole = e.getAnnotation(HasRole.class);
    Produces produces = e.getAnnotation(Produces.class);
    Consumes consumes = e.getAnnotation(Consumes.class);
    RequestMethodDetails requestMethodDetails = getRequestMethodDetails(e);
    ExecutableElement ee = (ExecutableElement) e;

    String path = basePath;
    if (!requestMethodDetails.path().isBlank()) {
      path = path + requestMethodDetails.path();
    }

    TypeMirror returnType = ee.getReturnType();
    if (TypeKind.VOID == returnType.getKind()) {
      return new MethodRequest(
          ee.getSimpleName().toString(),
          requestMethodDetails.method(),
          path,
          null == hasRole ? null : Arrays.stream(hasRole.value()).distinct().toList(),
          null == produces ? null : produces.value(),
          null == consumes ? null : consumes.value(),
          requestMethodDetails.responseCode(),
          true,
          null,
          getMethodParameters(ee.getParameters()));
    }

    return new MethodRequest(
        ee.getSimpleName().toString(),
        requestMethodDetails.method(),
        path,
        null == hasRole ? null : Arrays.stream(hasRole.value()).distinct().toList(),
        null == produces ? null : produces.value(),
        null == consumes ? null : consumes.value(),
        requestMethodDetails.responseCode(),
        false,
        className(returnType),
        getMethodParameters(ee.getParameters()));
  }

  private static List<MethodParameter> getMethodParameters(List<? extends VariableElement> ve) {
    return ve.stream()
        .map(
            e -> {
              Body body = e.getAnnotation(Body.class);
              return new MethodParameter(
                  className(e.asType()),
                  e.getSimpleName().toString(),
                  e.getAnnotation(QueryParams.class),
                  e.getAnnotation(PathParams.class),
                  e.getAnnotation(Headers.class),
                  e.getAnnotation(RoutingContext.class),
                  body,
                  e.getAnnotation(Session.class),
                  e.getAnnotation(Request.class),
                  e.getAnnotation(Response.class),
                  e.getAnnotation(UserContext.class),
                  e.getAnnotation(User.class),
                  null != body && e.getAnnotation(Valid.class) != null);
            })
        .toList();
  }

  private static ClassName className(TypeMirror typeMirror) {
    TypeName typeName = TypeName.get(typeMirror);
    String canonicalName = typeName.toString();
    int idx = canonicalName.lastIndexOf('.');
    if (-1 == idx) {
      return ClassName.get("", canonicalName);
    }
    String packageName = canonicalName.substring(0, idx);
    String simpleName = canonicalName.substring(idx + 1);
    return ClassName.get(packageName, simpleName);
  }
}
