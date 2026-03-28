# Gradle Migration

1. the maven plugin needs to be migrated to a gradle task
2. we need to also migrate the `byte-buddy-maven-plugin` and `advice-transformer` to be gradle tasks
3. we have duplicate junit platform test configurations
   1. move to `buildSrc` and conventions file
