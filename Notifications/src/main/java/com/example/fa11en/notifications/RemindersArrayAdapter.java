package com.example.fa11en.notifications;

// Import required libraries
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * An array adapter of type Reminder (custom object) that takes an xml file and fits the contents
 * of each object in the array to the view.
 */
public class RemindersArrayAdapter extends ArrayAdapter<Reminder> {

    // declare variables for handling views
    private final Context context;
    private final ArrayList<Reminder> reminds;

    /**
     * Constructor that creates the view from the reminder_layout.xml file
     * @param context
     * @param reminds
     */
    public RemindersArrayAdapter (Context context, ArrayList<Reminder> reminds) {
        super(context, R.layout.reminder_layout, reminds);
        this.context = context;
        this.reminds = reminds;
    }

    // created for custom actions when superclass function called
    @Override
    public void notifyDataSetChanged () {
        super.notifyDataSetChanged();
    }

    /**
     * Inflates the layout and sets the components of the layout to values from the array
     * pre: the array is of a proper format
     * post: returns a view for the mainActivity to display
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    @NonNull
    public View getView (final int position, View convertView, ViewGroup parent) {
        View remindView = null;

        // inflate the remindView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        remindView = inflater.inflate(R.layout.reminder_layout, parent, false);

        // set the components of the layout to values from the reminds array
        TextView dateTime = (TextView) remindView.findViewById(R.id.dateTime);
        TextView contactName = (TextView) remindView.findViewById(R.id.contactName);
        TextView message = (TextView) remindView.findViewById(R.id.message);
        dateTime.setText(TextUtils.join(":", reminds.get(position).time) + "  " + TextUtils.join("/", reminds.get(position).date));
        contactName.setText(reminds.get(position).name);
        message.setText(reminds.get(position).message);

        // when the view is clicked start the EditActivity activity and give it the data required to
        // populate its fields
        remindView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Reminder r = reminds.get(position);
                Intent editIntent = new Intent(context, EditActivity.class);
                editIntent.putExtra("date", TextUtils.join("/", r.date));
                editIntent.putExtra("time", TextUtils.join(":", r.time));
                editIntent.putExtra("name", r.name);
                editIntent.putExtra("number", r.number);
                editIntent.putExtra("message", r.message);
                editIntent.putExtra("index", position);
                context.startActivity(editIntent);
            }
        });

        return remindView;
    }

}
