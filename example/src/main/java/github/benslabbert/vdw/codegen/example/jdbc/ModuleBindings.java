/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import dagger.Binds;
import dagger.Module;

@Module
interface ModuleBindings {

  @Binds
  AddressRepository addressRepository(AddressRepositoryImpl addressRepository);

  @Binds
  PersonRepository personRepository(PersonRepositoryImpl personRepository);
}
