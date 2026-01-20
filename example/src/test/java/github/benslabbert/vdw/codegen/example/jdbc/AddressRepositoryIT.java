/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import github.benslabbert.vdw.codegen.commons.jdbc.EntityNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.Test;

class AddressRepositoryIT extends PostgresTestBase {

  @Test
  void save() {
    Address address =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    assertThat(address).isNotNull();
    assertThat(address.id()).isOne();
    assertThat(address.version()).isZero();
    assertThat(address.postalCode()).isEqualTo("pc1");
    assertThat(address.street()).isEqualTo("s1");
  }

  @Test
  void saveAll() {
    Collection<Address> address =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .insertAll(
                            List.of(
                                AddressBuilder.builder().postalCode("pc1").street("s1").build(),
                                AddressBuilder.builder().postalCode("pc2").street("s2").build())));

    assertThat(address)
        .hasSize(2)
        .satisfiesExactly(
            a -> {
              assertThat(a.id()).isOne();
              assertThat(a.version()).isZero();
              assertThat(a.postalCode()).isEqualTo("pc1");
              assertThat(a.street()).isEqualTo("s1");
            },
            a -> {
              assertThat(a.id()).isEqualTo(2L);
              assertThat(a.version()).isZero();
              assertThat(a.postalCode()).isEqualTo("pc2");
              assertThat(a.street()).isEqualTo("s2");
            });

    Iterator<Address> itr = address.iterator();
    Address a1 = itr.next();
    Address a2 = itr.next();

    Collection<Address> updated =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .updateAll(
                            List.of(
                                AddressBuilder.toBuilder(a1)
                                    .postalCode("pc-new-1")
                                    .street("s-new-1")
                                    .build(),
                                AddressBuilder.toBuilder(a2)
                                    .postalCode("pc-new-2")
                                    .street("s-new-2")
                                    .build())));

    assertThat(updated)
        .hasSize(2)
        .satisfiesExactly(
            a -> {
              assertThat(a.id()).isOne();
              assertThat(a.version()).isOne();
              assertThat(a.postalCode()).isEqualTo("pc-new-1");
              // street is immutable
              assertThat(a.street()).isEqualTo("s1");
            },
            a -> {
              assertThat(a.id()).isEqualTo(2L);
              assertThat(a.version()).isOne();
              assertThat(a.postalCode()).isEqualTo("pc-new-2");
              // street is immutable
              assertThat(a.street()).isEqualTo("s2");
            });
  }

  @Test
  void findByStreet() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().street("s1"));

    assertThat(found).isPresent().get().isEqualTo(saved);
  }

  @Test
  void findByStreet_notFound() {
    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().street("nonexistent"));

    assertThat(found).isEmpty();
  }

  @Test
  void findByPostalCode() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().postal_code("pc1"));

    assertThat(found).isPresent().get().isEqualTo(saved);
  }

  @Test
  void findByPostalCode_notFound() {
    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().postal_code("nonexistent"));

    assertThat(found).isEmpty();
  }

  @Test
  void findAll() {
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .addressRepository()
                    .insertAll(
                        List.of(
                            AddressBuilder.builder().postalCode("pc1").street("s1").build(),
                            AddressBuilder.builder().postalCode("pc2").street("s2").build(),
                            AddressBuilder.builder().postalCode("pc3").street("s3").build())));

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> {
              try (Stream<Address> all = provider.addressRepository().all()) {
                assertThat(all)
                    .hasSize(3)
                    .extracting(Address::street)
                    .containsExactly("s1", "s2", "s3");
              }
            });
  }

  @Test
  void findById() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().id(saved.id()));

    assertThat(found).isPresent().get().isEqualTo(saved);
  }

  @Test
  void findById_notFound() {
    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().id(999L));

    assertThat(found).isEmpty();
  }

  @Test
  void requireId() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Address found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().requireId(saved.id()));

    assertThat(found).isEqualTo(saved);
  }

  @Test
  void requireId_throwsWhenNotFound() {
    assertThatThrownBy(
            () ->
                provider
                    .jdbcTransactionManager()
                    .executeWithResult(() -> provider.addressRepository().requireId(999L)))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("not found id 999");
  }

  @Test
  void findByIdAndVersion() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () -> provider.addressRepository().idAndVersion(saved.id(), saved.version()));

    assertThat(found).isPresent().get().isEqualTo(saved);
  }

  @Test
  void findByIdAndVersion_wrongVersion() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().idAndVersion(saved.id(), 99));

    assertThat(found).isEmpty();
  }

  @Test
  void requireIdAndVersion() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Address found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider.addressRepository().requireIdAndVersion(saved.id(), saved.version()));

    assertThat(found).isEqualTo(saved);
  }

  @Test
  void requireIdAndVersion_throwsWhenNotFound() {
    assertThatThrownBy(
            () ->
                provider
                    .jdbcTransactionManager()
                    .executeWithResult(
                        () -> provider.addressRepository().requireIdAndVersion(999L, 0)))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("not found id 999 and version 0");
  }

  @Test
  void update_withOptimisticLocking() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    assertThat(saved.version()).isZero();

    Address updated =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.toBuilder(saved).postalCode("pc2").build()));

    assertThat(updated.id()).isEqualTo(saved.id());
    assertThat(updated.version()).isOne();
    assertThat(updated.postalCode()).isEqualTo("pc2");
    assertThat(updated.street()).isEqualTo("s1"); // street is immutable
  }

  @Test
  void update_optimisticLockingFailure() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    // Simulate concurrent update by updating in separate transaction
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .addressRepository()
                    .save(AddressBuilder.toBuilder(saved).postalCode("pc2").build()));

    // Try to update with stale version
    assertThatThrownBy(
            () ->
                provider
                    .jdbcTransactionManager()
                    .executeWithoutResult(
                        () ->
                            provider
                                .addressRepository()
                                .save(AddressBuilder.toBuilder(saved).postalCode("pc3").build())))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void saveWithCte_insert() {
    Long id =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .saveWithCte(
                            AddressBuilder.builder().postalCode("pc1").street("s1").build(),
                            "select id from cte_address",
                            new ScalarHandler<>()));

    assertThat(id).isNotNull().isEqualTo(1L);
  }

  @Test
  void saveWithCte_update() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Long result =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .saveWithCte(
                            AddressBuilder.toBuilder(saved).postalCode("pc2").build(),
                            "select id from cte_address",
                            new ScalarHandler<>()));

    assertThat(result).isEqualTo(saved.id());
  }

  @Test
  void delete() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    int deleted =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().delete(saved));

    assertThat(deleted).isOne();

    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().id(saved.id()));

    assertThat(found).isEmpty();
  }

  @Test
  void delete_optimisticLockingFailure() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    // Update to increment version
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .addressRepository()
                    .save(AddressBuilder.toBuilder(saved).postalCode("pc2").build()));

    // Try to delete with stale version
    int deleted =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().delete(saved));

    assertThat(deleted).isZero(); // No rows affected due to version mismatch
  }

  @Test
  void delete_throwsOnNewEntity() {
    Address newAddress = AddressBuilder.builder().postalCode("pc1").street("s1").build();

    assertThatThrownBy(
            () ->
                provider
                    .jdbcTransactionManager()
                    .executeWithoutResult(() -> provider.addressRepository().delete(newAddress)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("cannot delete new object");
  }

  @Test
  void deleteWithCte() {
    Address saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .save(AddressBuilder.builder().postalCode("pc1").street("s1").build()));

    Long deletedId =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .deleteWithCte(saved, "select id from cte_address", new ScalarHandler<>()));

    assertThat(deletedId).isEqualTo(saved.id());

    Optional<Address> found =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().id(saved.id()));

    assertThat(found).isEmpty();
  }

  @Test
  void deleteAll() {
    Collection<Address> saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .insertAll(
                            List.of(
                                AddressBuilder.builder().postalCode("pc1").street("s1").build(),
                                AddressBuilder.builder().postalCode("pc2").street("s2").build())));

    int[] deleted =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().deleteAll(saved));

    assertThat(deleted).containsExactly(1, 1);

    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () -> {
              try (Stream<Address> all = provider.addressRepository().all()) {
                assertThat(all).isEmpty();
              }
            });
  }

  @Test
  void deleteAll_optimisticLockingFailure() {
    Collection<Address> saved =
        provider
            .jdbcTransactionManager()
            .executeWithResult(
                () ->
                    provider
                        .addressRepository()
                        .insertAll(
                            List.of(
                                AddressBuilder.builder().postalCode("pc1").street("s1").build(),
                                AddressBuilder.builder().postalCode("pc2").street("s2").build())));

    Iterator<Address> iterator = saved.iterator();
    Address first = iterator.next();

    // Update first address to increment version
    provider
        .jdbcTransactionManager()
        .executeWithoutResult(
            () ->
                provider
                    .addressRepository()
                    .save(AddressBuilder.toBuilder(first).postalCode("pc-updated").build()));

    // Try to delete all with stale versions
    int[] deleted =
        provider
            .jdbcTransactionManager()
            .executeWithResult(() -> provider.addressRepository().deleteAll(saved));

    assertThat(deleted).containsExactly(0, 1); // First fails due to version mismatch
  }

  @Test
  void doInTransaction_save() {
    Address address =
        provider
            .addressRepository()
            .doInTransaction()
            .save(AddressBuilder.builder().postalCode("pc1").street("s1").build());

    assertThat(address).isNotNull();
    assertThat(address.id()).isOne();
    assertThat(address.version()).isZero();
    assertThat(address.postalCode()).isEqualTo("pc1");
    assertThat(address.street()).isEqualTo("s1");
  }

  @Test
  void doInTransaction_findById() {
    Address saved =
        provider
            .addressRepository()
            .doInTransaction()
            .save(AddressBuilder.builder().postalCode("pc1").street("s1").build());

    Optional<Address> found = provider.addressRepository().doInTransaction().id(saved.id());

    assertThat(found).isPresent().get().isEqualTo(saved);
  }

  @Test
  void doInTransaction_update() {
    Address saved =
        provider
            .addressRepository()
            .doInTransaction()
            .save(AddressBuilder.builder().postalCode("pc1").street("s1").build());

    assertThat(saved.version()).isZero();

    Address updated =
        provider
            .addressRepository()
            .doInTransaction()
            .save(AddressBuilder.toBuilder(saved).postalCode("pc2").build());

    assertThat(updated.id()).isEqualTo(saved.id());
    assertThat(updated.version()).isOne();
    assertThat(updated.postalCode()).isEqualTo("pc2");
    assertThat(updated.street()).isEqualTo("s1"); // street is immutable
  }

  @Test
  void doInTransaction_delete() {
    Address saved =
        provider
            .addressRepository()
            .doInTransaction()
            .save(AddressBuilder.builder().postalCode("pc1").street("s1").build());

    int deleted = provider.addressRepository().doInTransaction().delete(saved);

    assertThat(deleted).isOne();

    Optional<Address> found = provider.addressRepository().doInTransaction().id(saved.id());

    assertThat(found).isEmpty();
  }
}
