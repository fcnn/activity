package com.hk.activity

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestNetHandler {

  @BeforeEach
  fun setup(vertx: Vertx, testContext: VertxTestContext) {
    //vertx.deployVerticle(MainVerticle(), testContext.succeeding { _ -> testContext.completeNow() })
    testContext.completeNow()
  }

  @Test
  fun test_handler(vertx: Vertx, testContext: VertxTestContext) {
    val buffer = io.vertx.core.buffer.impl.BufferImpl()
    val msg = "hello,world! 你好，世界！😛".toByteArray()
    val len = msg.size
    buffer.appendBytes(byteArrayOf(1,0,0,0))
    buffer.appendBytes(byteArrayOf((len and 0xff).toByte(),((len and 0xff00) shr 8).toByte(),0,0))
    buffer.appendBytes(msg)
    val handler = NetHandler(null)
    handler.handle(buffer)

    testContext.completeNow()
  }
}
