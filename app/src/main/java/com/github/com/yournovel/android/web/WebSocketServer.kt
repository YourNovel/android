package com.github.com.yournovel.android.web

import fi.iki.elonen.NanoWSD
import com.github.com.yournovel.android.service.WebService
import com.github.com.yournovel.android.web.socket.*

class WebSocketServer(port: Int) : NanoWSD(port) {

    override fun openWebSocket(handshake: IHTTPSession): WebSocket? {
        WebService.serve()
        return when (handshake.uri) {
            "/bookSourceDebug" -> {
                BookSourceDebugWebSocket(handshake)
            }
            "/rssSourceDebug" -> {
                RssSourceDebugWebSocket(handshake)
            }
            "/searchBook" -> {
                BookSearchWebSocket(handshake)
            }
            else -> null
        }
    }
}
