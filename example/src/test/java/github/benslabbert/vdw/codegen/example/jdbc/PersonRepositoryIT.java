/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import github.benslabbert.vdw.codegen.commons.jdbc.EntityNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.Test;

class PersonRepositoryIT extends PostgresTestBase {

  private Address createAddress(String street, String postalCode) {
    return provider
        .jdbcTransactionManager()
        .executeWithResult(
            () ->
                provider
                    .addressRepository()
                    .save(AddressBuilder.builder().street(street).postalCode(postalCode).build()));
  }

  @Test
  void save() {
    Address address = createAddress("street1", "pc1");

    Person person =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .middleName("M")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    assertThat(person).isNotNull();
    assertThat(person.id()).isEqualTo(2L);
    assertThat(person.version()).isZero();
    assertThat(person.name()).isEqualTo("John");
    assertThat(person.middleName()).isEqualTo("M");
    assertThat(person.lastName()).isEqualTo("Doe");
    assertThat(person.age()).isEqualTo(30);
    assertThat(person.gender()).isEqualTo("male");
    assertThat(person.address().id()).isEqualTo(address.id());
  }

  @Test
  void insertAll() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    Collection<Person> people =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .insertAll(
                            List.of(
                                PersonBuilder.builder()
                                    .name("John")
                                    .lastName("Doe")
                                    .age(30)
                                    .gender("male")
                                    .address(addr1)
                                    .build(),
                                PersonBuilder.builder()
                                    .name("Jane")
                                    .lastName("Smith")
                                    .age(25)
                                    .gender("female")
                                    .address(addr2)
                                    .build())));

    assertThat(people)
        .hasSize(2)
        .satisfiesExactly(
            p -> {
              assertThat(p.id()).isEqualTo(3L);
              assertThat(p.version()).isZero();
              assertThat(p.name()).isEqualTo("John");
              assertThat(p.lastName()).isEqualTo("Doe");
            },
            p -> {
              assertThat(p.id()).isEqualTo(4L);
              assertThat(p.version()).isZero();
              assertThat(p.name()).isEqualTo("Jane");
              assertThat(p.lastName()).isEqualTo("Smith");
            });
  }

  @Test
  void updateAll_withImmutableFields() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    Collection<Person> saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .insertAll(
                            List.of(
                                PersonBuilder.builder()
                                    .name("John")
                                    .lastName("Doe")
                                    .age(30)
                                    .gender("male")
                                    .address(addr1)
                                    .build(),
                                PersonBuilder.builder()
                                    .name("Jane")
                                    .lastName("Smith")
                                    .age(25)
                                    .gender("female")
                                    .address(addr2)
                                    .build())));

    Iterator<Person> itr = saved.iterator();
    Person p1 = itr.next();
    Person p2 = itr.next();

    Collection<Person> updated =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .updateAll(
                            List.of(
                                PersonBuilder.toBuilder(p1)
                                    .name("NewJohn") // immutable, should not update
                                    .middleName("Middle1")
                                    .lastName("NewDoe") // immutable, should not update
                                    .age(31)
                                    .gender("other") // immutable, should not update
                                    .build(),
                                PersonBuilder.toBuilder(p2)
                                    .name("NewJane") // immutable, should not update
                                    .middleName("Middle2")
                                    .lastName("NewSmith") // immutable, should not update
                                    .age(26)
                                    .gender("other") // immutable, should not update
                                    .build())));

    assertThat(updated)
        .hasSize(2)
        .satisfiesExactly(
            p -> {
              assertThat(p.id()).isEqualTo(p1.id());
              assertThat(p.version()).isOne();
              assertThat(p.name()).isEqualTo("John"); // immutable
              assertThat(p.middleName()).isEqualTo("Middle1");
              assertThat(p.lastName()).isEqualTo("Doe"); // immutable
              assertThat(p.age()).isEqualTo(31);
              assertThat(p.gender()).isEqualTo("male"); // immutable
            },
            p -> {
              assertThat(p.id()).isEqualTo(p2.id());
              assertThat(p.version()).isOne();
              assertThat(p.name()).isEqualTo("Jane"); // immutable
              assertThat(p.middleName()).isEqualTo("Middle2");
              assertThat(p.lastName()).isEqualTo("Smith"); // immutable
              assertThat(p.age()).isEqualTo(26);
              assertThat(p.gender()).isEqualTo("female"); // immutable
            });
  }

  @Test
  void update_withOptimisticLocking() {
    Address address = createAddress("street1", "pc1");

    Person saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    assertThat(saved.version()).isZero();

    Person updated =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(PersonBuilder.toBuilder(saved).middleName("M").age(31).build()));

    assertThat(updated.id()).isEqualTo(saved.id());
    assertThat(updated.version()).isOne();
    assertThat(updated.age()).isEqualTo(31);
    assertThat(updated.middleName()).isEqualTo("M");
  }

  @Test
  void update_optimisticLockingFailure() {
    Address address = createAddress("street1", "pc1");

    Person saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    // Simulate concurrent update
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> provider.personRepository().save(PersonBuilder.toBuilder(saved).age(31).build()));

    // Try to update with stale version
    assertThatThrownBy(
            () ->
                provider
                    .jdbcTransactionManager()
                    .executeWithoutResult(
                        () ->
                            provider
                                .personRepository()
                                .save(PersonBuilder.toBuilder(saved).age(32).build())))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void findById() {
    Address address = createAddress("street1", "pc1");
    Person saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    Optional<Person> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().id(saved.id()));

    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(saved.id());
    assertThat(found.get().version()).isEqualTo(saved.version());
    assertThat(found.get().name()).isEqualTo(saved.name());
  }

  @Test
  void requireId() {
    Address address = createAddress("street1", "pc1");
    Person saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    Person found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().requireId(saved.id()));

    assertThat(found.id()).isEqualTo(saved.id());
    assertThat(found.version()).isEqualTo(saved.version());
    assertThat(found.name()).isEqualTo(saved.name());
  }

  @Test
  void requireId_throwsWhenNotFound() {
    assertThatThrownBy(
            () ->
                provider
                    .jdbcTransactionManager()
                    .executeWithResult(() -> provider.personRepository().requireId(999L)))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("not found id 999");
  }

  @Test
  void findByIdAndVersion() {
    Address address = createAddress("street1", "pc1");
    Person saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    Optional<Person> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () -> provider.personRepository().idAndVersion(saved.id(), saved.version()));

    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(saved.id());
    assertThat(found.get().version()).isEqualTo(saved.version());
  }

  @Test
  void requireIdAndVersion_throwsWhenNotFound() {
    assertThatThrownBy(
            () ->
                provider
                    .jdbcTransactionManager()
                    .executeWithResult(
                        () -> provider.personRepository().requireIdAndVersion(999L, 0)))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("not found id 999 and version 0");
  }

  @Test
  void delete() {
    Address address = createAddress("street1", "pc1");
    Person saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    int deleted =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().delete(saved));

    assertThat(deleted).isOne();
  }

  @Test
  void delete_optimisticLockingFailure() {
    Address address = createAddress("street1", "pc1");
    Person saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    // Update to increment version
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> provider.personRepository().save(PersonBuilder.toBuilder(saved).age(31).build()));

    // Try to delete with stale version
    int deleted =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().delete(saved));

    assertThat(deleted).isZero();
  }

  @Test
  void deleteAll() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    Collection<Person> saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .insertAll(
                            List.of(
                                PersonBuilder.builder()
                                    .name("John")
                                    .lastName("Doe")
                                    .age(30)
                                    .gender("male")
                                    .address(addr1)
                                    .build(),
                                PersonBuilder.builder()
                                    .name("Jane")
                                    .lastName("Smith")
                                    .age(25)
                                    .gender("female")
                                    .address(addr2)
                                    .build())));

    int[] deleted =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().deleteAll(saved));

    assertThat(deleted).containsExactly(1, 1);
  }

  @Test
  void saveWithCte_insert() {
    Address address = createAddress("street1", "pc1");

    Long id =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .saveWithCte(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build(),
                            "select id from c",
                            new ScalarHandler<Long>()));

    assertThat(id).isNotNull().isEqualTo(2L);
  }

  @Test
  void deleteWithCte() {
    Address address = createAddress("street1", "pc1");
    Person saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .save(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(address)
                                .build()));

    Long deletedId =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .personRepository()
                        .deleteWithCte(saved, "select id from c", new ScalarHandler<>()));

    assertThat(deletedId).isEqualTo(saved.id());
  }

  @Test
  void findByFirstName() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Smith")
                                .age(25)
                                .gender("male")
                                .address(addr2)
                                .build())));

    List<Person> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().first_name("John"));

    assertThat(found).hasSize(2).extracting(Person::name).containsOnly("John");
  }

  @Test
  void findByLastName() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Doe")
                                .age(25)
                                .gender("female")
                                .address(addr2)
                                .build())));

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> {
              try (Stream<Person> found = provider.personRepository().last_name("Doe")) {
                assertThat(found).hasSize(2).extracting(Person::lastName).containsOnly("Doe");
              }
            });
  }

  @Test
  void findByAge() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(30)
                                .gender("female")
                                .address(addr2)
                                .build())));

    AtomicInteger count = new AtomicInteger();
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .age(
                        30,
                        person -> {
                          assertThat(person.age()).isEqualTo(30);
                          count.incrementAndGet();
                        }));

    assertThat(count.get()).isEqualTo(2);
  }

  @Test
  void findByGender() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");
    Address addr3 = createAddress("street3", "pc3");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(25)
                                .gender("female")
                                .address(addr2)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jack")
                                .lastName("Jones")
                                .age(35)
                                .gender("male")
                                .address(addr3)
                                .build())));

    Iterable<Person> males =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().gender("male"));

    assertThat(males).hasSize(2).extracting(Person::gender).containsOnly("male");
  }

  @Test
  void queryAdults() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");
    Address addr3 = createAddress("street3", "pc3");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(25)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(18)
                                .gender("female")
                                .address(addr2)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jack")
                                .lastName("Jones")
                                .age(30)
                                .gender("male")
                                .address(addr3)
                                .build())));

    Iterable<Person> adults =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().adults());

    assertThat(adults).hasSize(2).extracting(Person::age).containsOnly(25, 30);
  }

  @Test
  void queryByAgeGroup() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");
    Address addr3 = createAddress("street3", "pc3");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(25)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(35)
                                .gender("female")
                                .address(addr2)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jack")
                                .lastName("Jones")
                                .age(45)
                                .gender("male")
                                .address(addr3)
                                .build())));

    List<Person> ageGroup =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().byAgeGroup(20, 40));

    assertThat(ageGroup).hasSize(2).extracting(Person::age).containsOnly(25, 35);
  }

  @Test
  void queryMen() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");
    Address addr3 = createAddress("street3", "pc3");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(25)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(35)
                                .gender("female")
                                .address(addr2)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jack")
                                .lastName("Jones")
                                .age(45)
                                .gender("male")
                                .address(addr3)
                                .build())));

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> {
              try (Stream<Person> men = provider.personRepository().men()) {
                assertThat(men).hasSize(2).extracting(Person::gender).containsOnly("male");
              }
            });
  }

  @Test
  void queryWomen() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");
    Address addr3 = createAddress("street3", "pc3");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(25)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(35)
                                .gender("female")
                                .address(addr2)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jill")
                                .lastName("Jones")
                                .age(45)
                                .gender("female")
                                .address(addr3)
                                .build())));

    AtomicInteger count = new AtomicInteger();
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .women(
                        person -> {
                          assertThat(person.gender()).isEqualTo("female");
                          count.incrementAndGet();
                        }));

    assertThat(count.get()).isEqualTo(2);
  }

  @Test
  void all() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(30)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(25)
                                .gender("female")
                                .address(addr2)
                                .build())));

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> {
              try (Stream<Person> all = provider.personRepository().all()) {
                assertThat(all).hasSize(2);
              }
            });
  }

  @Test
  void adultsSqlFile() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(25)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(18)
                                .gender("female")
                                .address(addr2)
                                .build())));

    // adultsSqlFile uses file1.sql which selects by id parameter
    Iterable<Person> result =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().adultsSqlFile(3L));

    assertThat(result).hasSize(1).first().extracting(Person::id).isEqualTo(3L);
  }

  @Test
  void byAgeGroupSqlFile() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(25)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(35)
                                .gender("female")
                                .address(addr2)
                                .build())));

    // byAgeGroupSqlFile uses file.sql which selects ALL
    List<Person> result =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.personRepository().byAgeGroupSqlFile());

    assertThat(result).hasSize(2);
  }

  @Test
  void menSqlFile() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(25)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(35)
                                .gender("female")
                                .address(addr2)
                                .build())));

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> {
              try (Stream<Person> men = provider.personRepository().menSqlFile()) {
                assertThat(men).hasSize(2);
              }
            });
  }

  @Test
  void womenSqlFile() {
    Address addr1 = createAddress("street1", "pc1");
    Address addr2 = createAddress("street2", "pc2");

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .personRepository()
                    .insertAll(
                        List.of(
                            PersonBuilder.builder()
                                .name("John")
                                .lastName("Doe")
                                .age(25)
                                .gender("male")
                                .address(addr1)
                                .build(),
                            PersonBuilder.builder()
                                .name("Jane")
                                .lastName("Smith")
                                .age(35)
                                .gender("female")
                                .address(addr2)
                                .build())));

    AtomicInteger count = new AtomicInteger();
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> provider.personRepository().womenSqlFile(_ -> count.incrementAndGet()));

    assertThat(count.get()).isEqualTo(2);
  }

  @Test
  void foreignKeyConstraint() {
    // Try to create a person without a valid address
    assertThatThrownBy(
            () ->
                provider
                    .jdbcTransactionManager()
                    .executeWithoutResult(
                        () ->
                            provider
                                .personRepository()
                                .save(
                                    PersonBuilder.builder()
                                        .name("John")
                                        .lastName("Doe")
                                        .age(30)
                                        .gender("male")
                                        .address(() -> 999L) // Non-existent address
                                        .build())))
        .isInstanceOf(RuntimeException.class);
  }
}
