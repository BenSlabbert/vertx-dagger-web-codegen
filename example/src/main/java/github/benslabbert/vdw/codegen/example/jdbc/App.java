/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dagger.internal.Provider;
import github.benslabbert.txmanager.PlatformTransactionManager;
import github.benslabbert.txmanager.annotation.AfterCommit;
import github.benslabbert.txmanager.annotation.BeforeCommit;
import github.benslabbert.txmanager.annotation.Transactional;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcQueryRunner;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcQueryRunnerFactory;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcQueryRunnerFactory_Impl;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcQueryRunner_Factory;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcTransactionManager_Factory;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcUtilsFactory;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcUtilsFactory_Impl;
import github.benslabbert.vertxdaggercommons.transaction.blocking.jdbc.JdbcUtils_Factory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.apache.commons.dbutils.StatementConfiguration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.conf.StatementType;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  private static final Logger log = LoggerFactory.getLogger(App.class);

  private final AddressRepository addressRepository;
  private final PersonRepository personRepository;
  private final JdbcQueryRunner jdbcQueryRunner;
  private final HikariDataSource dataSource;
  private final JooqRepo jooqRepo;

  private App() {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
    hikariConfig.setUsername("postgres");
    hikariConfig.setPassword("postgres");
    hikariConfig.setAutoCommit(false);
    hikariConfig.setMaximumPoolSize(1);
    hikariConfig.setPoolName("jdbc");
    hikariConfig.setThreadFactory(Thread.ofVirtual().name("v-", 0L).factory());

    this.dataSource = new HikariDataSource(hikariConfig);
    JdbcTransactionManager_Factory transactionManager =
        JdbcTransactionManager_Factory.create(() -> dataSource);

    Provider<JdbcQueryRunnerFactory> jdbcQueryRunnerFactoryProvider =
        JdbcQueryRunnerFactory_Impl.createFactoryProvider(
            JdbcQueryRunner_Factory.create(transactionManager));

    Provider<JdbcUtilsFactory> jdbcUtilsFactoryProvider =
        JdbcUtilsFactory_Impl.createFactoryProvider(
            JdbcUtils_Factory.create(() -> JdbcTransactionManager_Factory.newInstance(dataSource)));

    this.personRepository =
        new PersonRepositoryImpl(
            jdbcQueryRunnerFactoryProvider.get(), jdbcUtilsFactoryProvider.get());
    this.addressRepository =
        new AddressRepositoryImpl(
            jdbcQueryRunnerFactoryProvider.get(), jdbcUtilsFactoryProvider.get());
    PlatformTransactionManager.setTransactionManager(transactionManager.get());

    Settings settings =
        new Settings()
            .withParamType(ParamType.INDEXED)
            .withStatementType(StatementType.PREPARED_STATEMENT);
    DSLContext dslContext = DSL.using((DataSource) null, SQLDialect.POSTGRES, settings);
    this.jooqRepo = JooqRepo.builder().dslContext(dslContext).build();

    this.jdbcQueryRunner =
        jdbcQueryRunnerFactoryProvider.get().create(new StatementConfiguration.Builder().build());
  }

  public static void main(String[] args) {
    // docker container run --rm -it  -e POSTGRES_PASSWORD=postgres  -e POSTGRES_USER=postgres  -e
    // POSTGRES_DB=jdbc  -p 5432:5432  postgres:17

    App app = new App();
    app.run();
    log.info("done");
  }

  @Transactional
  private void run() {
    QueryParts statement = jooqRepo.personById();
    var statement2 = jooqRepo.personById();
    log.info("statement == statement2 {}", statement == statement2);
    String sql = statement.sql();
    List<Object> params = statement.params();
    log.info("sql {} params {}", sql, params);
    List<Long> execute =
        jdbcQueryRunner.query(
            sql,
            rs -> {
              List<Long> ids = new ArrayList<>();
              while (rs.next()) {
                long aLong = rs.getLong(1);
                log.info("rs {}", aLong);
                ids.add(aLong);
              }
              return ids;
            },
            1L);
    log.info("execute {}", execute);

    Address address =
        addressRepository.save(Address.builder().postalCode("pc1").street("s1").build());

    Person save =
        personRepository.save(
            Person.builder()
                .name("name")
                .lastName("other")
                .age(21)
                .gender("female")
                .middleName("middle")
                .address(address)
                .build());

    save = save.toBuilder().name("new_name").age(22).build();
    save = personRepository.save(save);

    try (Stream<Person> all = personRepository.all()) {
      all.forEach(p -> log.info("person {}", p));
    }

    personRepository.adults().forEach(a -> log.info("adult {}", a));

    List<Person> byAgeGroup = personRepository.byAgeGroup(0, 100);
    log.info("byAgeGroup {}", byAgeGroup);

    try (Stream<Person> men = personRepository.men()) {
      men.forEach(p -> log.info("men {}", p));
    }

    personRepository.women(w -> log.info("women {}", w));

    personRepository.first_name("name").forEach(p -> log.info("name {}", p));

    try (Stream<Person> s = personRepository.last_name("other")) {
      s.forEach(p -> log.info("lastName {}", p));
    }

    personRepository.age(22, p -> log.info("age {}", p));

    personRepository.gender("female").forEach(p -> log.info("gender {}", p));

    Optional<Person> id = personRepository.id(10);
    log.info("id {}", id);

    Optional<Person> idAndVersion = personRepository.idAndVersion(10, 1);
    log.info("idAndVersion {}", idAndVersion);

    int delete = personRepository.delete(save);
    log.info("delete {}", delete);

    Address address2 =
        addressRepository.save(Address.builder().postalCode("pc2").street("s2").build());
    Address address3 =
        addressRepository.save(Address.builder().postalCode("pc3").street("s3").build());

    Person p1 =
        Person.builder()
            .name("name1")
            .lastName("other1")
            .age(11)
            .address(address2)
            .gender("male")
            .build();
    Person p2 =
        Person.builder()
            .name("name2")
            .lastName("other2")
            .age(22)
            .address(address3)
            .gender("female")
            .build();

    Collection<Person> bulkSave = personRepository.insertAll(List.of(p1, p2));
    log.info("bulkSave {}", bulkSave);
    bulkSave = personRepository.updateAll(bulkSave);
    log.info("bulkSave after update {}", bulkSave);

    int[] ints = personRepository.deleteAll(bulkSave);
    log.info("deleteAll {}", ints);

    List<Person> name1 = personRepository.first_name("name1");
    log.info("name1 {}", name1);
    List<Person> name2 = personRepository.first_name("name2");
    log.info("name2 {}", name2);

    Projection projection =
        personRepository.saveWithCte(
            p1,
            """
            select cte_person.first_name, a.postal_code from cte_person
            join address a on a.id = cte_person.address_id
            where a.postal_code = ?
            """,
            rs -> {
              if (rs.next()) {
                String fname = rs.getString(1);
                String postalCode = rs.getString(2);
                return new Projection(fname, postalCode);
              }
              throw new SQLException("empty result set");
            },
            "pc2");
    log.info("projection {}", projection);

    try (var s = personRepository.all()) {
      s.forEach(personRepository::delete);
    }

    beforeCommit();
    afterCommit1();
    afterCommit2();
    afterCommit3();
  }

  private record Projection(String name, String postalCode) {}

  @BeforeCommit
  private void beforeCommit() {
    log.info("beforeCommit");
  }

  @AfterCommit
  private void afterCommit1() {
    log.info("afterCommit1");
    dataSource.close();
  }

  @AfterCommit
  private void afterCommit2() {
    log.info("afterCommit2");
  }

  @AfterCommit
  private void afterCommit3() {
    log.info("afterCommit3");
  }
}
