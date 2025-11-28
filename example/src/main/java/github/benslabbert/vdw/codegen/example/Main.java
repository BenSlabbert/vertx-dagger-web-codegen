/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example;

import github.benslabbert.vdw.codegen.example.jdbc.Person;
import github.benslabbert.vdw.codegen.launcher.CustomApplicationHooks;
import io.vertx.launcher.application.VertxApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends VertxApplication {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    log.info("Starting");
    int code = new Main(args).launch();
    log.info("launch successful ? {}", 0 == code);

    Person person =
        Person.builder().id(1L).name("name").lastName("lname").age(1).gender("M").build();
    log.info("person {}", person);
    Person newPerson = person.toBuilder().build();
    log.info("newPerson {}", newPerson);
  }

  private Main(String[] args) {
    super(args, new CustomApplicationHooks());
  }
}
