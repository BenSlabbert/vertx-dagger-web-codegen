# Gradle Migration

1. we have duplicate junit platform test configurations
   1. move to `buildSrc` and conventions file

# Use service loaders for advice

Right now we have this META-INF/advice_annotations file where we keep track of advice implementations.
It might be better to rather use ServiceLoaders for this.
