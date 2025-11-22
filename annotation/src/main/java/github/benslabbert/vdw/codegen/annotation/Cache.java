/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Cache {

  enum Policy {
    /// Update the cache value only on a normal return.
    ON_SUCCESS,
    /// Update the cache value regardless of whether the method exited normally.
    ALWAYS
  }

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  @interface Put {
    /// The name of the cache
    String value();

    /// The key to be used to store the cached value. This String can reference the indexes of
    /// certain params to be used when creating the string dynamically.
    ///
    /// Example:
    /// ```java
    ///  @Cache.Put(value = "cache", key = "k-#0-#1")
    ///  public String cached(String in, int value) {
    ///    return in;
    ///  }
    /// ```
    ///
    /// So when this method is called as:
    ///
    /// ```java
    /// String val = cached("data", 2);
    /// ```
    ///
    /// The cache key will be calculated as `k-data-2`.
    String key();

    /// If the cached should be updated in an asynchronous manner or in the same thread.
    boolean async() default false;
  }

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  @interface Evict {
    /// The name of the cache
    String value();

    /// The key to be used to store the cached value. This String can reference the indexes of
    /// certain params to be used when creating the string dynamically.
    ///
    /// Example:
    /// ```java
    ///  @Cache.Revoke(value = "cache", key = "k-#0-#1")
    ///  public void removeFromCache(String in, int value) {
    ///    // do work
    ///  }
    /// ```
    ///
    /// So when this method is called as:
    ///
    /// ```java
    /// removeFromCache("data", 2);
    /// ```
    ///
    /// The cache key will be calculated as `k-data-2` and removed from the cache.
    String key();

    ///  When the cache should be revoked.
    Policy policy() default Policy.ON_SUCCESS;

    /// If the cached should be updated in an asynchronous manner or in the same thread.
    boolean async() default false;
  }
}
