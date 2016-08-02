package pl.mirkoczat.mirkoczat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.phoenixframework.channels.Envelope
import java.util.*

class UsersFragment : Fragment(), AnkoLogger {
    var adapter : ArrayAdapter<UserData>? = null
    data class UserData(val name: String) {
        override fun toString() = "${name}"
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_users, container, false)
        val listView = view?.findViewById(R.id.users) as ListView
        var users = ArrayList<UserData>()
        adapter = ArrayAdapter<UserData>(context,
                android.R.layout.simple_list_item_1, android.R.id.text1, users)
        listView.adapter = adapter
        Stream.on("info:room", {
            val envelope = it as Envelope
            val user_list = envelope.payload.get("users")
            val users = user_list.map {
                UserData(it.get("login").asText())
            }.sortedBy { it.name }
            activity?.runOnUiThread {
                adapter?.clear()
                for (user in users) {
                    adapter?.add(user)
                }
                adapter?.notifyDataSetChanged()
            }
        })
        Stream.on("app:changeChannel", {
            activity?.runOnUiThread {
                adapter?.clear()
                adapter?.notifyDataSetChanged()
            }
        })
        info("onCreateView")
        return view
    }
}
