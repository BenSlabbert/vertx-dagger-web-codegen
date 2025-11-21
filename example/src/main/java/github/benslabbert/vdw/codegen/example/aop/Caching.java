/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop;

import github.benslabbert.vdw.codegen.annotation.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Caching {

  private static final Logger log = LoggerFactory.getLogger(Caching.class);

  static void main() {
    Caching caching = new Caching();
    log.info("before");
    String cached = caching.cached("data");
    log.info("before");
    caching.revoke();
  }

  @Cache.Put(value = "cache", key = "k-#0", async = true)
  public String cached(String in) {
    log.info("cached");
    return in;
  }

  @Cache.Evict(value = "cache", key = "k", policy = Cache.Policy.ALWAYS)
  public void revoke() {
    log.info("revoke");
  }
}
