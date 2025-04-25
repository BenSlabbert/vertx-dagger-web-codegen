/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.verticle;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DefaultVerticleIT {

  @Test
  void get(Vertx v, VertxTestContext tc) {
    DefaultVerticle verticle = new DefaultVerticle();
    v.deployVerticle(
            verticle,
            new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", 0))
                .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                .setWorkerPoolSize(1))
        .onComplete(tc.succeeding(ignore -> sendGetRequest(v, tc, verticle.getPort())));
  }

  @Test
  void post(Vertx v, VertxTestContext tc) {
    DefaultVerticle verticle = new DefaultVerticle();
    v.deployVerticle(
            verticle,
            new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", 0))
                .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                .setWorkerPoolSize(1))
        .onComplete(tc.succeeding(ignore -> sendPostRequest(v, tc, verticle.getPort())));
  }

  @Test
  void postInvalidData(Vertx v, VertxTestContext tc) {
    DefaultVerticle verticle = new DefaultVerticle();
    v.deployVerticle(
            verticle,
            new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", 0))
                .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                .setWorkerPoolSize(1))
        .onComplete(tc.succeeding(ignore -> sendInvalidPostRequest(v, tc, verticle.getPort())));
  }

  private void sendGetRequest(Vertx v, VertxTestContext tc, int port) {
    WebClient.create(v)
        .get(port, "127.0.0.1", "/handler/buffer")
        .basicAuthentication("name", "password")
        .send()
        .onComplete(
            tc.succeeding(
                resp ->
                    tc.verify(
                        () -> {
                          assertThat(resp.body()).hasToString("data");
                          tc.completeNow();
                        })));
  }

  private void sendPostRequest(Vertx v, VertxTestContext tc, int port) {
    WebClient.create(v)
        .post(port, "127.0.0.1", "/i/handler/data")
        .basicAuthentication("name", "password")
        .sendJsonObject(new JsonObject().put("data", "data"))
        .onComplete(
            tc.succeeding(
                resp ->
                    tc.verify(
                        () -> {
                          assertThat(resp.body()).hasToString("{\"data\":\"data\"}");
                          tc.completeNow();
                        })));
  }

  private void sendInvalidPostRequest(Vertx v, VertxTestContext tc, int port) {
    WebClient.create(v)
        .post(port, "127.0.0.1", "/i/handler/data")
        .basicAuthentication("name", "password")
        .sendJsonObject(new JsonObject().put("bad", "data"))
        .onComplete(
            tc.succeeding(
                resp ->
                    tc.verify(
                        () -> {
                          assertThat(resp.statusCode()).isEqualTo(400);
                          assertThat(resp.body())
                              .hasToString(
                                  "{\"errors\":[{\"field\":\"data\",\"message\":\"must not be"
                                      + " blank\"}]}");
                          tc.completeNow();
                        })));
  }
}
