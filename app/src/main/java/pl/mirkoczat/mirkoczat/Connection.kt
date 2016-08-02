package pl.mirkoczat.mirkoczat

import android.content.Context
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.jetbrains.anko.*
import org.phoenixframework.channels.Channel
import org.phoenixframework.channels.Envelope
import org.phoenixframework.channels.Socket

object Connection : AnkoLogger {
    var socket : Socket? = null
    var channel : Channel? = null
    var token = ""
    var onError: (Envelope) -> Unit = {}
    fun connect(context: Context, channelName: String) {
        disconnect()

        socket = Socket("ws://mirkoczat.pl/socket/websocket?tag=$channelName&token=$token")
        socket?.connect()
        channel = socket?.chan("rooms:" + channelName, null)
        channel?.join()?.receive("ignore") {
            envelope -> info("ignore ${envelope}")
        }?.receive("error") { envelope ->
            info("error ${envelope}")
            onError(envelope)
        }?.receive("ok") { envelope -> info("ok ${envelope}") }
        channel?.onClose { envelope -> info("closed ${envelope}") }
        channel?.onError { envelope ->
            info("error ${envelope}")
        }
        for (event in arrayOf("msg:send", "info:cmd", "info:global", "info:room")) {
            channel?.on(event) {envelope -> Stream.trigger(event, envelope)}
        }
    }

    fun disconnect() {
        channel?.leave()
        socket?.disconnect()
    }

    fun send(message: String): Boolean {
        channel?.push("msg:send", ObjectNode(JsonNodeFactory.instance)
            .put("body", message)
        )
        return true
    }


}