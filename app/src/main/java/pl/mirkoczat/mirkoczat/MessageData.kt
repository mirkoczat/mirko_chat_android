package pl.mirkoczat.mirkoczat
import android.graphics.Color
import com.fasterxml.jackson.databind.JsonNode
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.phoenixframework.channels.Envelope

class MessageData: AnkoLogger {
    var user = "SYSTEM"
    var sex:String = ""
    var color = Color.GRAY
    var body = ""
    constructor(env: Envelope) {
        val jn = env.getPayload()
        user = jn.get("user").asText()
        sex = jn.get("sex").asText()
        var color_text = jn.get("color").asText()
        if (color_text == "#fff")
            color_text = "#000000"
        if (color_text == "transparent")
            color_text = "#000000"
        color = Color.parseColor(color_text)
        body = jn.get("body").asText()
    }
    constructor(env: Envelope, type:Type) {
        val jn = env.getPayload()

        when (type) {
            Type.ERROR -> {
                val response = jn.get("response")
                body = response.get("reason").asText()
            }
            Type.SYSTEM -> body = jn.get("body").asText()
        }
    }
    enum class Type {
        ERROR, SYSTEM
    }
}