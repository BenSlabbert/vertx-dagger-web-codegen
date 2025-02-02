/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator;

import com.squareup.javapoet.ClassName;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Headers;
import github.benslabbert.vdw.codegen.annotation.WebRequest.PathParams;
import github.benslabbert.vdw.codegen.annotation.WebRequest.QueryParams;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Request;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Response;
import github.benslabbert.vdw.codegen.annotation.WebRequest.RoutingContext;
import github.benslabbert.vdw.codegen.annotation.WebRequest.Session;
import github.benslabbert.vdw.codegen.annotation.WebRequest.User;
import github.benslabbert.vdw.codegen.annotation.WebRequest.UserContext;

public record MethodParameter(
    ClassName type,
    String name,
    QueryParams queryParams,
    PathParams pathParams,
    Headers headers,
    RoutingContext routingContext,
    Body body,
    Session session,
    Request request,
    Response response,
    UserContext userContext,
    User user,
    boolean validateBody) {}
