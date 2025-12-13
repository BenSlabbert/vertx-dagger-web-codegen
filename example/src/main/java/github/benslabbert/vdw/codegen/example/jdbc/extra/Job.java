/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc.extra;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;

@Table("job")
@GenerateBuilder
public record Job(
    @Table.Column("id") @Table.Id("id_seq") long id,
    @Table.Column("version") @Table.Version int version)
    implements Reference<Job> {}
