/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import github.benslabbert.vdw.codegen.annotation.Table;
import github.benslabbert.vdw.codegen.annotation.Table.Column;
import github.benslabbert.vdw.codegen.annotation.Table.FindByColumn;
import github.benslabbert.vdw.codegen.annotation.Table.Id;
import github.benslabbert.vdw.codegen.annotation.Table.InsertOnly;
import github.benslabbert.vdw.codegen.annotation.Table.Query;
import github.benslabbert.vdw.codegen.annotation.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.annotation.Nonnull;
import java.util.List;

@Table("person")
@Query(name = "adults", sql = "SELECT * FROM person WHERE age > 21", returnType = List.class)
@Query(
    name = "byAgeGroup",
    sql = "SELECT * FROM person WHERE age > :minAge and age < :maxAge",
    returnType = List.class)
@Query(name = "men", sql = "SELECT * FROM person WHERE gender = 'male'", fetchSize = 100)
@Query(
    name = "women",
    sql = "SELECT * FROM person WHERE gender = 'female'",
    returnType = Iterable.class)
public record Person(
    @Column("id") @Id("id_seq") long id,
    @Column("first_name") @FindByColumn(returnType = List.class) @InsertOnly String name,
    @Column("last_name") @FindByColumn @InsertOnly String lastName,
    @Column("age") @FindByColumn(value = "ageAndGender", fetchSize = 25) int age,
    @Column("gender") @FindByColumn("ageAndGender") @InsertOnly String gender,
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
    return "Person{id=%d, name='%s', lastName='%s', age=%d, gender='%s', address=%d, version=%d}"
        .formatted(id, name, lastName, age, gender, address.id(), version);
  }

  public interface Builder {
    Builder id(long id);

    Builder name(String name);

    Builder lastName(String lastName);

    Builder age(int age);

    Builder gender(String gender);

    Builder address(Reference<Address> address);

    Builder version(int version);

    Person build();
  }
}
