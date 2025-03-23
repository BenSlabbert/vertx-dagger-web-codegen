/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generated {interface name}VertxEBClientProxy and {interface name}VertxEBProxyHandler. <br>
 * VertxEBClientProxy is used by the client to call the EB service. <br>
 * VertxEBProxyHandler is used on the server side. <br>
 * Additionally, {interface name}_EB_Module_Bindings is created to provide:
 *
 * <ul>
 *   <li>{@link java.util.Set} of {@link
 *       github.benslabbert.vdw.codegen.example.eb.EventBusServiceConfigurer}
 *   <li>{@link java.util.Set} of {@link io.vertx.serviceproxy.ProxyHandler}
 * </ul>
 *
 * to the dependency graph, as well as a concrete implementation of {@link
 * github.benslabbert.vdw.codegen.example.eb.EventBusServiceConfigurer} is created called {interface
 * name}_EventBusServiceConfigurerImpl <br>
 * The server will use this to register its implementation of the {interface name} on the EB.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface EventBusService {

  String address();
}
