package com.hk.activity

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import com.fy.engineserver.message.*

@ExtendWith(VertxExtension::class)
class TestSchedule {
  @BeforeEach
  fun setup(vertx: Vertx, testContext: VertxTestContext) {
    //vertx.deployVerticle(MainVerticle(), testContext.succeeding { _ -> testContext.completeNow() })
    testContext.completeNow()
    // 0：连续模式 1：每日模式
  }

  // 0：连续模式 1：每日模式
  @Test
  fun test_schedule(vertx: Vertx, testContext: VertxTestContext) {
    val schedule = Schedule()
    schedule.startDate = 0
    schedule.endDate = Schedule.getDayStartTimestamp() + Schedule.ONE_DAY_MS*70
    schedule.startDayOfWeek = 7
    schedule.startTime = 0
    schedule.endDayOfWeek = 2
    schedule.endTime = Schedule.ONE_DAY_MS
    val startTime = schedule.periodStartTime
    val endTime = schedule.periodEndTime
    //val startTime = 1648915200000
    //val endTime = 1649174400000
    println("(1649520000000,1649174400000) ~ ($startTime,$endTime):${Schedule.getDayStartTimestamp()} => ${Schedule.getPeriodInfo(startTime,endTime)})}")

    testContext.completeNow()
  }
  @Test
  fun test_message(vertx: Vertx, testContext: VertxTestContext) {
    val msg = TARGET_SKILL_REQ(GameMessageFactory.nextSequnceNum(),0f,0f,0f, ByteArray(0),LongArray(0),1,0f)
    testContext.completeNow()
  }
}
