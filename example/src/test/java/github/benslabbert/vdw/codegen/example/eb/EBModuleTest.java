/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.eb;

import static org.assertj.core.api.Assertions.assertThat;

import github.benslabbert.vdw.codegen.commons.eb.EventBusServiceConfigurer;
import github.benslabbert.vdw.codegen.example.di.DaggerProvider;
import github.benslabbert.vdw.codegen.example.di.Provider;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ProxyHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class EBModuleTest {

  Provider provider;

  @BeforeEach
  void init(Vertx v) {
    provider = DaggerProvider.builder().vertx(v).config(new JsonObject()).build();
    provider.eventBusServiceConfigurers().forEach(EventBusServiceConfigurer::configure);
  }

  @AfterEach
  void after() {
    provider.proxyHandlers().forEach(ProxyHandler::close);
  }

  @Test
  void invalid_ExampleServiceVertxEBClientProxy(Vertx v, VertxTestContext tc) {
    var clientProxy =
        new ExampleServiceVertxEBClientProxy(
            v, new DeliveryOptions().addHeader("auth-token", "token"));

    clientProxy
        .execute(new ExampleRequest("data"))
        .onComplete(
            tc.failing(
                err ->
                    tc.verify(
                        () -> {
                          assertThat(err).isNotNull();
                          assertThat(err).isInstanceOf(ReplyException.class);
                          ReplyException re = (ReplyException) err;
                          assertThat(re.failureCode()).isEqualTo(403);
                          assertThat(re.failureType()).isEqualTo(ReplyFailure.RECIPIENT_FAILURE);
                          assertThat(re.getMessage()).isEqualTo("Forbidden");
                          tc.completeNow();
                        })));
  }

  @Test
  void valid_DataServiceVertxEBClientProxy(Vertx v, VertxTestContext tc) {
    var clientProxy =
        new DataServiceVertxEBClientProxy(
            v, new DeliveryOptions().addHeader("auth-token", "token"));

    clientProxy
        .getData(new DataRequest("data"))
        .onComplete(
            tc.succeeding(
                response ->
                    tc.verify(
                        () -> {
                          assertThat(response).isNotNull();
                          assertThat(response.data()).isEqualTo("data");
                          tc.completeNow();
                        })));
  }

  @Test
  void invalid(Vertx v, VertxTestContext tc) {
    var clientProxy = new DataServiceVertxEBClientProxy(v);

    clientProxy
        .getData(new DataRequest("data"))
        .onComplete(
            tc.failing(
                err ->
                    tc.verify(
                        () -> {
                          assertThat(err).isNotNull();
                          assertThat(err).isInstanceOf(ReplyException.class);
                          ReplyException re = (ReplyException) err;
                          assertThat(re.failureCode()).isEqualTo(401);
                          assertThat(re.failureType()).isEqualTo(ReplyFailure.RECIPIENT_FAILURE);
                          assertThat(re.getMessage()).isEqualTo("token cannot be null or empty");
                          tc.completeNow();
                        })));
  }
}
