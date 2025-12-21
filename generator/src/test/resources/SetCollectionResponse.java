/* Licensed under Apache-2.0 2024. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import java.util.Set;

@JsonWriter
public record SetCollectionResponse(Set<ApplicationDetails> applications) {

  @JsonWriter
  public record ApplicationDetails(long id, int version, String name) {}
}

class SetCollectionResponseBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder applications(Set<SetCollectionResponse.ApplicationDetails> applications);

    SetCollectionResponse build();
  }
}

class SetCollectionResponse_ApplicationDetailsBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder id(long id);

    Builder version(int version);

    Builder name(String name);

    SetCollectionResponse.ApplicationDetails build();
  }
}
