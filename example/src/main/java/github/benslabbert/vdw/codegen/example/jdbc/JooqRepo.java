/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import jakarta.annotation.Nonnull;
import java.util.List;
import org.jooq.AttachableQueryPart;
import org.jooq.DSLContext;
import org.jooq.conf.ParamType;

@AutoValue
abstract class JooqRepo {

  static Builder builder() {
    return new $AutoValue_JooqRepo.Builder();
  }

  abstract DSLContext dslContext();

  @Nonnull
  @Memoized
  QueryParts personById() {
    AttachableQueryPart query = dslContext().select().from("person").where("id=?");
    String jdbcSql = query.getSQL(ParamType.INDEXED);
    List<Object> params = query.getBindValues();
    return new QueryParts(jdbcSql, params);
  }

  @AutoValue.Builder
  interface Builder {

    Builder dslContext(DSLContext dslContext);

    JooqRepo build();
  }
}
