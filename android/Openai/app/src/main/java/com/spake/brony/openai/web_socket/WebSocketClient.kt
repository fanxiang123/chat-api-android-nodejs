package com.spake.brony.openai.web_socket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString

class WebSocketClient(url: String) {
    private val client = OkHttpClient()
    private val request = Request.Builder().url(url).build()
    private var webSocket:WebSocket? = null


    fun connect(listener: WebSocketListener,callback: (WebSocket) -> Unit) {
        if (webSocket ==null)
         webSocket = client.newWebSocket(request, listener)

        callback(webSocket!!)

    }

    fun close() {
        client.dispatcher().executorService().shutdown()
        client.connectionPool().evictAll()
    }
}

