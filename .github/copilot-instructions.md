# Copilot Instructions for vertx-dagger-web-codegen

## Project Overview

This is a Java/Maven multi-module project that provides code generation tools for Vert.x web applications with Dagger dependency injection. The project includes annotation processors, AOP support, and various generators for web handlers, event bus consumers, JSON serialization, and more.

## Project Structure

- `annotation/` - Custom annotations for code generation
- `generator/` - Annotation processors and code generators
- `commons/` - Shared utilities
- `aop/` - Aspect-oriented programming support
- `advice-extractor-plugin/` - Maven plugin for extracting advice
- `advice-transformer/` - Bytecode transformation utilities
- `txmanager/` - Transaction management
- `logging/` - Logging utilities
- `launcher/` - Application launcher utilities
- `bom/` - Bill of Materials for dependency management
- `example/` - Example usage

## Build and Test Commands

```bash
# Build and test (requires GH_TOKEN environment variable for GitHub packages)
mvn -s settings.xml clean install

# Build with code style check
mvn -s settings.xml clean spotless:check install

# Run specific Java version build
mvn -s settings.xml clean install -Djava.version=21

# Run only tests
mvn -s settings.xml test

# Run integration tests
mvn -s settings.xml verify
```

## Java Version Requirements

- The project supports Java versions: 21, 23, 24, and 25
- Use the latest supported Java language features
- Default target is Java 21 for compatibility

## Coding Conventions

### Code Style

- **Formatter**: Google Java Format (enforced via Spotless)
- **License headers**: Run `mvn spotless:apply` to automatically add/fix license headers
- **Import organization**: Automatic via google-java-format
- **Modifier ordering**: Reorder modifiers automatically
- **Javadoc formatting**: Enabled

### Naming Conventions

- Package names: `github.benslabbert.vdw.codegen.*`
- Use descriptive class names ending with their purpose (e.g., `ProcessorBase`, `GenerationException`)
- Use `var` for local variables when the type is obvious

### Dependencies

- Use `jakarta.annotation` for annotations (not `javax.annotation`)
- Prefer immutable collections
- Use records for simple data carriers

### Testing

- Tests use JUnit 5
- Use the `compile-testing` library for annotation processor tests
- Integration tests are configured with failsafe plugin

## Important Notes

- The `settings.xml` file is required for Maven commands as it configures GitHub Package Registry access
- The `GH_TOKEN` environment variable must be set for dependency resolution from GitHub packages
- Code formatting is enforced - run `spotless:check` before committing
- The project uses Dagger for dependency injection - follow Dagger patterns for new modules

