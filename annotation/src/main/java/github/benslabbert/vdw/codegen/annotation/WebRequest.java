/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface WebRequest {

  @Target(ElementType.PARAMETER)
  @interface QueryParams {}

  @Target(ElementType.PARAMETER)
  @interface PathParams {}

  @Target(ElementType.PARAMETER)
  @interface Headers {}

  @Target(ElementType.PARAMETER)
  @interface Body {}

  @Target(ElementType.PARAMETER)
  @interface RoutingContext {}

  @Target(ElementType.PARAMETER)
  @interface Request {}

  @Target(ElementType.PARAMETER)
  @interface Response {}

  @Target(ElementType.PARAMETER)
  @interface UserContext {}

  @Target(ElementType.PARAMETER)
  @interface Session {}

  /**
   * all Http methods will be routed here, use:
   *
   * <ul>
   *   <li>{@link io.vertx.ext.web.RoutingContext}
   *   <li>{@link io.vertx.core.http.HttpServerRequest}
   * </ul>
   *
   * to determine the actual HTTP method.
   */
  @Target(ElementType.METHOD)
  @interface All {

    String METHOD = "All";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Get {

    String METHOD = "GET";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Post {

    String METHOD = "POST";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Put {

    String METHOD = "PUT";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Delete {

    String METHOD = "DELETE";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Patch {

    String METHOD = "PATCH";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Options {

    String METHOD = "OPTIONS";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Head {

    String METHOD = "HEAD";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Trace {

    String METHOD = "TRACE";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Connect {

    String METHOD = "CONNECT";

    String path() default "";

    int responseCode() default 200;
  }

  @Target(ElementType.METHOD)
  @interface Produces {
    String value();
  }

  @Target(ElementType.METHOD)
  @interface Consumes {
    String value();
  }
}
