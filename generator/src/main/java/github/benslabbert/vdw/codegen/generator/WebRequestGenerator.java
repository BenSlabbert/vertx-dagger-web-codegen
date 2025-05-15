/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import github.benslabbert.vdw.codegen.annotation.WebRequest;
import github.benslabbert.vdw.codegen.annotation.WebRequest.All;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Connect;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Delete;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Head;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Options;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Patch;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Put;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Trace;
import github.benslabbert.vdw.codegen.commons.BooleanParser;
import github.benslabbert.vdw.codegen.commons.DoubleParser;
import github.benslabbert.vdw.codegen.commons.FloatParser;
import github.benslabbert.vdw.codegen.commons.InstantParser;
import github.benslabbert.vdw.codegen.commons.IntegerParser;
import github.benslabbert.vdw.codegen.commons.LongParser;
import github.benslabbert.vdw.codegen.commons.RequestParser;
import github.benslabbert.vdw.codegen.commons.StringParser;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import jakarta.annotation.Generated;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;

public class WebRequestGenerator extends ProcessorBase {

  public WebRequestGenerator() {
    super(
        Set.of(
            All.class.getCanonicalName(),
            Get.class.getCanonicalName(),
            Post.class.getCanonicalName(),
            Put.class.getCanonicalName(),
            Patch.class.getCanonicalName(),
            Delete.class.getCanonicalName(),
            Head.class.getCanonicalName(),
            Trace.class.getCanonicalName(),
            WebRequest.Consumes.class.getCanonicalName()));
  }

  @Override
  List<GeneratedFile> generateTempFile(Element e) {
    String path = getPath(e);

    PathParser.ParseResult parseResult = PathParser.parse(path);

    if (parseResult.pathParams().isEmpty() && parseResult.queryParams().isEmpty()) {
      return List.of();
    }

    Name methodName = e.getSimpleName();
    Element enclosingElement = e.getEnclosingElement();
    Name enclosingClassName = enclosingElement.getSimpleName();
    String canonicalName = enclosingElement.asType().toString();
    String classPackage = canonicalName.substring(0, canonicalName.lastIndexOf('.'));

    String string =
        methodName.toString().substring(0, 1).toUpperCase() + methodName.toString().substring(1);

    String generatedClassName = enclosingClassName.toString() + "_" + string + "_" + "ParamParser";
    String generatedRecordName = enclosingClassName.toString() + "_" + string + "_" + "Params";

    StringWriter stringWriter = StringWriterFactory.create();

    try (PrintWriter out = new PrintWriter(stringWriter)) {
      out.printf("package %s;%n", classPackage);
      out.println();

      out.printf("import %s;%n", IntegerParser.class.getCanonicalName());
      out.printf("import %s;%n", LongParser.class.getCanonicalName());
      out.printf("import %s;%n", RequestParser.class.getCanonicalName());
      out.printf("import %s;%n", StringParser.class.getCanonicalName());
      out.printf("import %s;%n", BooleanParser.class.getCanonicalName());
      out.printf("import %s;%n", FloatParser.class.getCanonicalName());
      out.printf("import %s;%n", DoubleParser.class.getCanonicalName());
      out.printf("import %s;%n", InstantParser.class.getCanonicalName());
      out.printf("import %s;%n", RequestParser.class.getCanonicalName());
      out.printf("import %s;%n", MultiMap.class.getCanonicalName());
      out.printf("import %s;%n", Map.class.getCanonicalName());
      out.printf("import %s;%n", RoutingContext.class.getCanonicalName());
      out.printf("import %s;%n", Instant.class.getCanonicalName());
      out.printf("import %s;%n", Generated.class.getCanonicalName());
      out.println();

      out.printf(
          "@Generated(value = \"%s\", date = \"%s\")%n",
          getClass().getCanonicalName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
      out.printf("final class %s {%n", generatedClassName);
      out.println();
      out.println("\tprivate " + generatedClassName + "() {}");
      out.println();

      if (parseResult.pathParams().isEmpty()) {
        out.printf("\tstatic %s parse(MultiMap queryParams) {%n", generatedRecordName);
        out.println("\t\tRequestParser rp = RequestParser.create(queryParams);");
      } else if (parseResult.queryParams().isEmpty()) {
        out.printf("\tstatic %s parse(Map<String, String> pathParams) {%n", generatedRecordName);
        out.println("\t\tRequestParser rp = RequestParser.create(pathParams);");
      } else {
        out.printf(
            "\tstatic %s parse(MultiMap queryParams, Map<String, String> pathParams) {%n",
            generatedRecordName);
        out.println("\t\tRequestParser rp = RequestParser.create(queryParams, pathParams);");
      }
      out.println();

      printGetParams(parseResult, out);

      out.println();
      out.printf("\t\treturn new %s(", generatedRecordName);
      String args =
          Stream.concat(parseResult.pathParams().stream(), parseResult.queryParams().stream())
              .map(PathParser.Param::name)
              .collect(Collectors.joining(", "));
      out.printf("%s", args);
      out.println(");");
      out.println("\t}");
      out.println();

      printRecord(out, generatedRecordName, parseResult);

      out.println("}");
    }

    return List.of(new GeneratedFile(stringWriter, classPackage + "." + generatedClassName));
  }

  private String getPath(Element e) {
    All all = e.getAnnotation(All.class);
    if (null != all) {
      return all.path();
    }
    Get get = e.getAnnotation(Get.class);
    if (null != get) {
      return get.path();
    }
    Post post = e.getAnnotation(Post.class);
    if (null != post) {
      return post.path();
    }
    Put put = e.getAnnotation(Put.class);
    if (null != put) {
      return put.path();
    }
    Patch patch = e.getAnnotation(Patch.class);
    if (null != patch) {
      return patch.path();
    }
    Delete delete = e.getAnnotation(Delete.class);
    if (null != delete) {
      return delete.path();
    }
    Head head = e.getAnnotation(Head.class);
    if (null != head) {
      return head.path();
    }
    Trace trace = e.getAnnotation(Trace.class);
    if (null != trace) {
      return trace.path();
    }
    Options options = e.getAnnotation(Options.class);
    if (null != options) {
      return options.path();
    }
    Connect connect = e.getAnnotation(Connect.class);
    if (null != connect) {
      return connect.path();
    }

    printError("no WebRequest method for element", e);
    throw new GenerationException("no WebRequest method for element");
  }

  private static void printRecord(
      PrintWriter out, String generatedRecordName, PathParser.ParseResult parseResult) {
    // print the generated record type
    out.printf("\trecord %s(", generatedRecordName);

    String recordArgs =
        Stream.concat(parseResult.pathParams().stream(), parseResult.queryParams().stream())
            .map(
                p -> {
                  String name = p.name();
                  String type =
                      switch (p.type()) {
                        case INT -> "int";
                        case LONG -> "long";
                        case STRING -> "String";
                        case BOOLEAN -> "boolean";
                        case FLOAT -> "float";
                        case DOUBLE -> "double";
                        case TIMESTAMP -> "Instant";
                      };

                  return type + " " + name;
                })
            .collect(Collectors.joining(", "));

    out.printf("%s", recordArgs);
    out.println(") {}");
  }

  private static void printGetParams(PathParser.ParseResult parseResult, PrintWriter out) {
    for (PathParser.Param pathParam : parseResult.pathParams()) {
      String name = pathParam.name();
      String type =
          switch (pathParam.type()) {
            case INT -> "Integer";
            case LONG -> "Long";
            case BOOLEAN -> "Boolean";
            case FLOAT -> "Float";
            case DOUBLE -> "Double";
            case TIMESTAMP -> "Instant";
            case STRING -> "String";
          };

      Optional<String> optional = pathParam.defaultValue();
      if (optional.isPresent()) {
        if (pathParam.type() == PathParser.Type.STRING) {
          out.printf(
              "\t\t%s %s = rp.getPathParam(\"%s\", \"%s\", %sParser.create());%n",
              type, name, name, optional.get(), type);
        } else {
          out.printf(
              "\t\t%s %s = rp.getPathParam(\"%s\", %s, %sParser.create());%n",
              type, name, name, optional.get(), type);
        }
      } else {
        out.printf(
            "\t\t%s %s = rp.getPathParam(\"%s\", %sParser.create());%n", type, name, name, type);
      }
    }

    for (PathParser.Param queryParam : parseResult.queryParams()) {
      String name = queryParam.name();
      String type =
          switch (queryParam.type()) {
            case INT -> "Integer";
            case LONG -> "Long";
            case BOOLEAN -> "Boolean";
            case FLOAT -> "Float";
            case DOUBLE -> "Double";
            case TIMESTAMP -> "Instant";
            case STRING -> "String";
          };

      Optional<String> optional = queryParam.defaultValue();
      if (optional.isPresent()) {
        if (queryParam.type() == PathParser.Type.STRING) {
          out.printf(
              "\t\t%s %s = rp.getQueryParam(\"%s\", \"%s\", %sParser.create());%n",
              type, name, name, optional.get(), type);
        } else {
          out.printf(
              "\t\t%s %s = rp.getQueryParam(\"%s\", %s, %sParser.create());%n",
              type, name, name, optional.get(), type);
        }
      } else {
        out.printf(
            "\t\t%s %s = rp.getQueryParam(\"%s\", %sParser.create());%n", type, name, name, type);
      }
    }
  }
}
