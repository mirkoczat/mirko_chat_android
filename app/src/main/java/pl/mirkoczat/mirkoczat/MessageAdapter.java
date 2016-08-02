package pl.mirkoczat.mirkoczat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<MessageData> {
    Context context;
    int layoutResourceId;
    ArrayList<MessageData> data;
    public MessageAdapter(Context context, int layoutResourceId, ArrayList<MessageData> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MessageHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MessageHolder();
            holder.avatar = (ImageView)row.findViewById(R.id.avatar);
            holder.username = (TextView)row.findViewById(R.id.username);
            holder.body = (TextView)row.findViewById(R.id.body);

            row.setTag(holder);
        } else {
            holder = (MessageHolder) row.getTag();
        }

        MessageData message = data.get(position);
        //holder.avatar = ?
        holder.username.setText(message.getUser());
        holder.username.setTextColor(message.getColor());
        holder.body.setText(message.getBody());

        return row;
    }

    static class MessageHolder {
        ImageView avatar;
        TextView username;
        TextView body;
    }
}
