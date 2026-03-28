# Gradle Migration

3. the maven plugin needs to be migrated to a gradle task
4. we need to also migrate the `byte-buddy-maven-plugin` and `advice-transformer` to be gradle tasks
5. we have duplicate junit platform test configurations
   1. move to `buildSrc` and conventions file
6. java compiler options are unclear
   1. in maven we have proc configs, parameters, etc, we need a gradle equivalent
7. not sure how [jvm.config](./.mvn/jvm.config) to gradle configs
