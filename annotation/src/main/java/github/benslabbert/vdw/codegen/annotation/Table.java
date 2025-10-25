/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Table {

  int DEFAULT_FETCH_SIZE = 10;

  /// The name of the table in the database
  String value() default "";

  /// The name of the CTE for the table
  /// defaults to cte_Table#value()
  String cte() default "";

  @Target(ElementType.RECORD_COMPONENT)
  @interface Id {
    String value();
  }

  @Target(ElementType.RECORD_COMPONENT)
  @interface Column {
    String value();
  }

  @Target(ElementType.TYPE)
  @interface Queries {
    Query[] value();
  }

  /** Provide either SQL or refer to a file that can be found in the program's resources. */
  @Repeatable(Table.Queries.class)
  @Target(ElementType.TYPE)
  @interface Query {
    String name();

    String sql() default "";

    String sqlFile() default "";

    int fetchSize() default DEFAULT_FETCH_SIZE;

    /** Supports: {@link List}, {@link Iterable}, {@link Stream}, {@link Consumer} */
    Class<?> returnType() default Stream.class;
  }

  @Target(ElementType.RECORD_COMPONENT)
  @interface FindByColumn {
    String value() default "";

    /** Supports: {@link List}, {@link Iterable}, {@link Stream}, {@link Consumer} */
    Class<?> returnType() default Stream.class;

    int fetchSize() default DEFAULT_FETCH_SIZE;
  }

  @Target(ElementType.RECORD_COMPONENT)
  @interface FindOneByColumn {
    String value() default "";
  }

  @Target(ElementType.RECORD_COMPONENT)
  @interface InsertOnly {}

  @Target(ElementType.RECORD_COMPONENT)
  @interface Version {}
}
