package com.example.fa11en.notifications;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RemindersArrayAdapter extends ArrayAdapter<Reminder> {

    private final Context context;
    private final ArrayList<Reminder> reminds;

    public RemindersArrayAdapter (Context context, ArrayList<Reminder> reminds) {
        super(context, R.layout.reminder_layout, reminds);
        this.context = context;
        this.reminds = reminds;
    }

    @Override
    public void notifyDataSetChanged () {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        View remindView = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        remindView = inflater.inflate(R.layout.reminder_layout, parent, false);

        TextView dateTime = (TextView) remindView.findViewById(R.id.dateTime);
        TextView contactName = (TextView) remindView.findViewById(R.id.contactName);
        TextView message = (TextView) remindView.findViewById(R.id.message);
        dateTime.setText(TextUtils.join(":", reminds.get(position).time) + "  " + TextUtils.join("/", reminds.get(position).date));
        contactName.setText(reminds.get(position).name);
        message.setText(reminds.get(position).message);

        return remindView;
    }
}
