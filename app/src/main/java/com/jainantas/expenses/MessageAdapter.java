package com.jainantas.expenses;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<MoneyDetails> {
    public MessageAdapter(Context context, int resource, List<MoneyDetails> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_display, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.name);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date);
        TextView amountTextView = (TextView) convertView.findViewById(R.id.amount);
        TextView detailTextView = (TextView) convertView.findViewById(R.id.msg);

        MoneyDetails message = getItem(position);

        nameTextView.setText(message.getName());
        dateTextView.setText(message.getDate());
        amountTextView.setText(message.getPrice());
        detailTextView.setText(message.getDetail());

        return convertView;
    }
}
