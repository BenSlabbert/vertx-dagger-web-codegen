/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.generator.web;

import com.palantir.javapoet.ClassName;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Headers;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.PathParams;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.QueryParams;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Request;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Response;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.RoutingContext;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Session;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.User;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.UserContext;

record MethodParameter(
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
