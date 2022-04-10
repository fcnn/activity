package com.hk.activity.proto

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestProto {

  @BeforeEach
  fun setup(vertx: Vertx, testContext: VertxTestContext) {
    //vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
    testContext.completeNow()
  }

  @Test
  fun test_proto(vertx: Vertx, testContext: VertxTestContext) {
    val builder = ActivityReq.newBuilder()
    with(builder) {
      id = 1
      name = "Max"
      addAmount(kotlin.math.E)
      addAllAmount(mutableListOf(kotlin.math.PI,kotlin.math.E))
    }
    val req = builder.build()
    val ba = req.toByteArray()
    println("$req size=${ba.size}")
    val req2=ActivityReq.parseFrom(ba)
    val ba2 = req2.toByteArray()
    println("$req2 size=${ba2.size}")

    testContext.completeNow()
  }
}
