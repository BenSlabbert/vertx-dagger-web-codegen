/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.Table;
import github.benslabbert.vdw.codegen.annotation.Table.Column;
import github.benslabbert.vdw.codegen.annotation.Table.FindByColumn;
import github.benslabbert.vdw.codegen.annotation.Table.Id;
import github.benslabbert.vdw.codegen.annotation.Table.InsertOnly;
import github.benslabbert.vdw.codegen.annotation.Table.Query;
import github.benslabbert.vdw.codegen.annotation.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@GenerateBuilder
@Table(value = "person", cte = "c")
@Query(
    name = "adults",
    sql = "SELECT * FROM person WHERE age > 21",
    fetchSize = 5,
    returnType = Iterable.class)
@Query(
    name = "byAgeGroup",
    sql = "SELECT * FROM person WHERE age > :minAge and age < :maxAge",
    fetchSize = 7,
    returnType = List.class)
@Query(name = "men", sql = "SELECT * FROM person WHERE gender = 'male'", fetchSize = 100)
@Query(
    name = "women",
    sql = "SELECT * FROM person WHERE gender = 'female'",
    returnType = Consumer.class)
@Query(name = "adultsSqlFile", sqlFile = "file1.sql", fetchSize = 5, returnType = Iterable.class)
@Query(name = "byAgeGroupSqlFile", sqlFile = "file.sql", fetchSize = 7, returnType = List.class)
@Query(name = "menSqlFile", sqlFile = "file.sql", fetchSize = 100)
@Query(name = "womenSqlFile", sqlFile = "file.sql", returnType = Consumer.class)
public record Person(
    @Column("id") @Id("id_seq") long id,
    @Column("first_name") @FindByColumn(fetchSize = 1, returnType = List.class) @InsertOnly
        String name,
    @Column("middle_name") @Nullable String middleName,
    @Column("last_name") @FindByColumn @InsertOnly String lastName,
    @Column("age") @FindByColumn(fetchSize = 25, returnType = Consumer.class) int age,
    @Column("gender") @FindByColumn(fetchSize = 50, returnType = Iterable.class) @InsertOnly
        String gender,
    @Column("address_id") @Nonnull Reference<Address> address,
    @Column("version") @Version int version)
    implements Reference<Person> {

  public static PersonBuilder.Builder builder() {
    return PersonBuilder.builder().id(0).version(0).address(Reference.create());
  }

  public PersonBuilder.Builder toBuilder() {
    return PersonBuilder.toBuilder(this);
  }

  @Nonnull
  @Override
  public String toString() {
    return "Person{id=%d, name='%s', lastName='%s', age=%d, gender='%s', address=%d, version=%d}"
        .formatted(id, name, lastName, age, gender, address.id(), version);
  }
}
