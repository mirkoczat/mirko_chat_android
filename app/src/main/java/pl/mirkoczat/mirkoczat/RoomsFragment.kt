package pl.mirkoczat.mirkoczat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.ctx
import org.phoenixframework.channels.Envelope
import java.util.*

class RoomsFragment : Fragment(), AnkoLogger {
    var adapter : ArrayAdapter<ChannelData>? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_rooms, container, false)
        val listView = view?.findViewById(R.id.rooms) as ListView
        val channels = ArrayList<ChannelData>()
        adapter = ArrayAdapter<ChannelData>(context,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                channels)
        adapter?.setNotifyOnChange(false)
        listView.adapter = adapter
        listView.setOnItemClickListener { adapterView, view, i, l ->
            val cd = adapterView.getItemAtPosition(i) as ChannelData
            Connection.connect(ctx, cd.name)
            Stream.trigger("app:changeChannel", cd.name)
        }
        Stream.on("info:global", {env -> onInfoGlobal(env as Envelope)})
        return view
    }
    fun onInfoGlobal(envelope: Envelope) {
        val rooms = envelope.payload.get("rooms")
        val channels = rooms.map {
            val name = it.get("name").asText()
            val count = it.get("count").asInt()
            val count_all = it.get("count_all").asInt()
            ChannelData(name, count, count_all)
        }
        activity?.runOnUiThread {
            adapter?.clear()
            for (room in channels) {
                adapter?.add(room)
            }
            adapter?.notifyDataSetChanged()
        }
    }
}