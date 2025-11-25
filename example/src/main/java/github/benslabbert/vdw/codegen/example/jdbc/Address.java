/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import github.benslabbert.vdw.codegen.annotation.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.Table;
import github.benslabbert.vdw.codegen.annotation.Table.Column;
import github.benslabbert.vdw.codegen.annotation.Table.FindOneByColumn;
import github.benslabbert.vdw.codegen.annotation.Table.Id;
import github.benslabbert.vdw.codegen.annotation.Table.InsertOnly;
import github.benslabbert.vdw.codegen.annotation.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;

@Table("address")
@GenerateBuilder
public record Address(
    @Column("id") @Id("id_seq") long id,
    @Column("street") @FindOneByColumn @InsertOnly String street,
    @Column("postal_code") @FindOneByColumn String postalCode,
    @Column("version") @Version int version)
    implements Reference<Address> {

  public static AddressBuilder.Builder builder() {
    return AddressBuilder.builder().id(0).version(0);
  }

  public AddressBuilder.Builder toBuilder() {
    return AddressBuilder.toBuilder(this);
  }
}
