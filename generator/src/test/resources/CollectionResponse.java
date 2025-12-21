/* Licensed under Apache-2.0 2024. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import java.util.Collection;

@JsonWriter
public record CollectionResponse(Collection<ApplicationDetails> applications) {

  @JsonWriter
  public record ApplicationDetails(long id, int version, String name) {}
}

class CollectionResponseBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder applications(Collection<CollectionResponse.ApplicationDetails> applications);

    CollectionResponse build();
  }
}

class CollectionResponse_ApplicationDetailsBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder id(long id);

    Builder version(int version);

    Builder name(String name);

    CollectionResponse.ApplicationDetails build();
  }
}
