package github.benslabbert.vdw.codegen.example.verticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
class DefaultVerticleIT {

  private static final Logger log = LoggerFactory.getLogger(DefaultVerticleIT.class);

  @Test
  void t(Vertx vertx, VertxTestContext testContext) {
    Checkpoint checkpoint = testContext.checkpoint(2);

    vertx
        .deployVerticle(
            new DefaultVerticle(),
            new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", 0))
                .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                .setWorkerPoolSize(1))
        .onComplete(
            testContext.succeeding(
                s -> {
                  log.info("deployment id: {}", s);
                  checkpoint.flag();
                }));

    checkpoint.flag();
  }
}
