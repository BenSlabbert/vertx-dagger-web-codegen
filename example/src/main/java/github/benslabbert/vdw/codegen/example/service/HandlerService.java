/* Licensed under Apache-2.0 2024. */
package github.benslabbert.vdw.codegen.example.service;

import github.benslabbert.txmanager.annotation.Transactional;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
public class HandlerService {

  private static final Logger log = LoggerFactory.getLogger(HandlerService.class);

  @Inject
  HandlerService() {}

  @Transactional
  public void process() {
    log.info("do transactional work");
  }
}
