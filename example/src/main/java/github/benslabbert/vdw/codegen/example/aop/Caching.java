/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.aop;

import github.benslabbert.vdw.codegen.annotation.Cache;

public class Caching {

  @Cache.Put(value = "cache", key = "k-#0", async = true)
  public String cached(String in) {
    return in;
  }

  @Cache.Evict(value = "cache", key = "k", policy = Cache.Policy.ALWAYS)
  public void revoke() {}
}
