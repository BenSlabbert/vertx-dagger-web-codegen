# Gradle Migration

1. google test does not work, looks like an issue with JDK internal APIs
2. annotation processing fails when loading resources
```java
@Query(name = "adultsSqlFile", sqlFile = "file1.sql", fetchSize = 5, returnType = Iterable.class)
```
3. the maven plugin needs to be migrated to a gradle task
4. we need to also migrate the `byte-buddy-maven-plugin` and `advice-transformer` to be gradle tasks
5. we have duplicate junit platform test configurations
   1. move to `buildSrc` and conventions file
6. java compiler options are unclear
   1. in maven we have proc configs, parameters, etc, we need a gradle equivalent
7. not sure how [jvm.config](./.mvn/jvm.config) to gradle configs
