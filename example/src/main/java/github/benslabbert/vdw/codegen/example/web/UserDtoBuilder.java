/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.web;

final class UserDtoBuilder {

  private UserDtoBuilder() {}

  public static Builder builder() {
    return new BuilderImpl();
  }

  public interface Builder {
    Builder username(String username);

    Builder email(String email);

    Builder active(boolean active);

    UserDto build();
  }

  private static final class BuilderImpl implements Builder {
    private String username;
    private String email;
    private boolean active;

    @Override
    public Builder username(String username) {
      this.username = username;
      return this;
    }

    @Override
    public Builder email(String email) {
      this.email = email;
      return this;
    }

    @Override
    public Builder active(boolean active) {
      this.active = active;
      return this;
    }

    @Override
    public UserDto build() {
      return new UserDtoImpl(username, email, active);
    }
  }

  private static final class UserDtoImpl implements UserDto {
    private final String username;
    private final String email;
    private final boolean active;

    UserDtoImpl(String username, String email, boolean active) {
      this.username = username;
      this.email = email;
      this.active = active;
    }

    @Override
    public String username() {
      return username;
    }

    @Override
    public String email() {
      return email;
    }

    @Override
    public boolean active() {
      return active;
    }
  }
}
