package com.hk.activity

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.WebSocketConnectOptions
import io.vertx.core.http.WebsocketVersion
import java.security.MessageDigest
import java.util.Base64

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
        initClients(startPromise)
      } else {
        startPromise.fail(it.cause())
      }
    }
  }

  private val maxClient = 2
  private fun initClients(promise: Promise<Void>) {
    val options = WebSocketConnectOptions()
    //options.setPort(443).host = "socketsbay.com"
    //options.setPort(5678).host = "192.168.0.221"
    options.setPort(443).host = "demo.piesocket.com"
    options.isSsl = true
    options.allowOriginHeader = true
    //options.uri = "wss://socketsbay.com/wss/v2/2/demo/"
    //options.uri = "/"
    options.uri = "wss://demo.piesocket.com/v3/channel_1?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self"
    options.version = WebsocketVersion.V13
    //options.addHeader("Sec-WebSocket-Key","dGhlIHNhbXBsZSBub25jZQ==")

    for (i in 1..maxClient) {
      val client = vertx.createHttpClient()
      client.webSocket(options) {
       if (it.succeeded()) {
         val ws = it.result()
         println("success:${ws.remoteAddress()}")
       } else {
         println("error: ${it.cause()}")
       }
      }
    }

    val sha1 = MessageDigest.getInstance("sha1")
    val k = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
    sha1.update(((if (options.headers==null) null else options.headers["Sec-WebSocket-Key"]) +k).toByteArray())
    val o = String(Base64.getEncoder().encode(sha1.digest()))
    println("----------------[$o]--------------")
    promise.complete()
  }

  override fun stop(stopPromise: Promise<Void>) {
    stopPromise.complete()
  }
}
