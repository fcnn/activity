package com.hk.activity

import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket

// 0：连续模式 1：每日模式
class NetHandler(private val sock: NetSocket?) : io.vertx.core.Handler<Buffer> {
  private var buffer = ByteArray(256)
  private var headerOk = false
  private var tag:Int = 0
  private var len:Int = 0
  private var dataLen:Int = 0
  override fun handle(event: Buffer) {
    var offset = 0
    while (true) {
      // header：tag+len
      var start = offset
      if (!headerOk) {
        offset += 8 - dataLen
        if (offset > event.length()) {
          offset = event.length()
        } else {
          headerOk = true
        }
        for (i in start until offset) {
          buffer[dataLen++] = event.getByte(i)
        }
        if (!headerOk) {
          return
        }
        parseHeader()
        dataLen = 0
        if (len>buffer.size) {
          buffer = ByteArray(len)
        }
      }
      //data
      if (offset >= event.length()) {
        break
      }
      start = offset
      offset += len - dataLen;
      if (offset > event.length()) {
        offset = event.length()
      }
      for (i in start until offset) {
        buffer[dataLen++] = event.getByte(i)
      }
      if (dataLen < len) {
        break;
      }
      decodeMsg()
      len = 0
      if (dataLen > 256) {
        buffer = ByteArray(256)
      }
      dataLen = 0
      headerOk = false
    }
  }

  private fun decodeMsg() {
    val msg = String(buffer,0,dataLen)
    println(":::: tag=${tag} len=${len} [${msg}]")
  }

  private fun parseHeader() {
    tag = (buffer[0].toUInt() or (buffer[1].toUInt() shl 8) or (buffer[2].toUInt() shl 16) or (buffer[3].toUInt() shl 24)).toInt()
    len = (buffer[4].toUInt() or (buffer[5].toUInt() shl 8) or (buffer[6].toUInt() shl 16) or (buffer[7].toUInt() shl 24)).toInt()
  }
}
