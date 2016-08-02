package pl.mirkoczat.mirkoczat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ListView
import org.jetbrains.anko.AnkoLogger
import org.phoenixframework.channels.Envelope
import java.util.*

class MessagesFragment : Fragment(), AnkoLogger {
    var adapter : MessageAdapter? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_messages, container, false)
        var messages = ArrayList<MessageData>()
        adapter = MessageAdapter(context, R.layout.message_item, messages)
        adapter?.setNotifyOnChange(false)
        val listView = view?.findViewById(R.id.messages) as ListView
        listView.adapter = adapter
        val editText = view?.findViewById(R.id.inputMsg) as EditText
        editText.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_SEND) {
                Connection.send(textView.text.toString())
                textView.text = ""
                handled = true
            }
            if (keyEvent?.keyCode == 66) {
                Connection.send(textView.text.toString())
                textView.text = ""
                handled = true
            }
            handled
        }
        Stream.on("msg:send", {
            activity?.runOnUiThread {
                adapter?.add(MessageData(it as Envelope))
                adapter?.notifyDataSetChanged()
            }
        })
        Stream.on("app:changeChannel", {
            activity?.runOnUiThread {
                adapter?.clear()
                adapter?.notifyDataSetChanged()
            }
        })
        return view
    }
}