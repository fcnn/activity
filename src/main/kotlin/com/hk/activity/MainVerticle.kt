package com.hk.activity

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

const val serverPort = 10000
const val serverName = "game activity server"

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx.createNetServer().connectHandler{ socket ->
      socket.handler(NetHandler(socket))
    }.listen(serverPort) {
      if (it.succeeded()) {
        val server = it.result()
        println("======== $serverName started on port:${server.actualPort()} ========")
        startPromise.complete()
      } else {
        startPromise.fail(it.cause())
      }
    }
  }
}
