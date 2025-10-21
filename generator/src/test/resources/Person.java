/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import github.benslabbert.vdw.codegen.annotation.Table;
import github.benslabbert.vdw.codegen.annotation.Table.Column;
import github.benslabbert.vdw.codegen.annotation.Table.FindByColumn;
import github.benslabbert.vdw.codegen.annotation.Table.FindOneByColumn;
import github.benslabbert.vdw.codegen.annotation.Table.Id;
import github.benslabbert.vdw.codegen.annotation.Table.InsertOnly;
import github.benslabbert.vdw.codegen.annotation.Table.Query;
import github.benslabbert.vdw.codegen.annotation.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

@Table("person")
@Query(name = "adults", sql = "SELECT * FROM person WHERE age > 21", returnType = Iterable.class)
@Query(
    name = "byAgeGroup",
    sql = "SELECT * FROM person WHERE age > :minAge and age < :maxAge",
    returnType = List.class)
@Query(name = "men", sql = "SELECT * FROM person WHERE gender = 'male'", fetchSize = 100)
@Query(
    name = "women",
    sql = "SELECT * FROM person WHERE gender = 'female'",
    returnType = Consumer.class)
@Query(name = "adultsSqlFile", sqlFile = "file.sql", fetchSize = 5, returnType = Iterable.class)
@Query(name = "byAgeGroupSqlFile", sqlFile = "file.sql", fetchSize = 7, returnType = List.class)
@Query(name = "menSqlFile", sqlFile = "file.sql", fetchSize = 100)
@Query(name = "womenSqlFile", sqlFile = "file.sql", returnType = Consumer.class)
public record Person(
    @Column("id") @Id("id_seq") long id,
    @Column("first_name") @FindByColumn(returnType = List.class) @InsertOnly String name,
    @Column("last_name") @FindByColumn @InsertOnly String lastName,
    @Column("unique") @FindOneByColumn @InsertOnly String unique,
    @Column("age") @FindByColumn(fetchSize = 25, returnType = Consumer.class) int age,
    @Column("gender") @FindByColumn(returnType = Iterable.class) @InsertOnly String gender,
    @Column("address_id") @Nonnull Reference<Address> address,
    @Column("version") @Version int version)
    implements Reference<Person> {

  public record Address(long id) implements Reference<Address> {}

  public static Builder builder() {
    return null;
  }

  public Builder toBuilder() {
    return null;
  }

  @Nonnull
  @Override
  public String toString() {
    return "Person{id=%d, name='%s', lastName='%s', unique='%s', age=%d, gender='%s', address=%d, version=%d}"
        .formatted(id, name, lastName, unique, age, gender, address.id(), version);
  }

  public interface Builder {
    Builder id(long id);

    Builder name(String name);

    Builder lastName(String lastName);

    Builder unique(String unique);

    Builder age(int age);

    Builder gender(String gender);

    Builder address(Reference<Address> address);

    Builder version(int version);

    Person build();
  }
}
