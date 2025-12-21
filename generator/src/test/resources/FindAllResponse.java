/* Licensed under Apache-2.0 2024. */
package my.test;

import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import java.util.List;

@JsonWriter
public record FindAllResponse(List<ApplicationDetails> applications) {

  @JsonWriter
  public record ApplicationDetails(long id, int version, String name) {}
}

class FindAllResponseBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder applications(List<FindAllResponse.ApplicationDetails> applications);

    FindAllResponse build();
  }
}

class FindAllResponse_ApplicationDetailsBuilder {

  public static Builder builder() {
    return null;
  }

  public interface Builder {
    Builder id(long id);

    Builder version(int version);

    Builder name(String name);

    FindAllResponse.ApplicationDetails build();
  }
}
