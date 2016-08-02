package pl.mirkoczat.mirkoczat

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.phoenixframework.channels.Envelope
import java.util.*

data class Binding(val event: String, val callback: (Any) -> Unit)

object Stream : AnkoLogger {
    val bindings =  ArrayList<Binding>()
    fun on(event: String, callback: (Any) -> Unit) {
        val binding = Binding(event, callback)
        bindings.add(binding)
    }
    fun trigger(event: String, data: Any) {
        bindings
            .filter { it.event == event }
            .map { it.callback.invoke(data) }
    }
}