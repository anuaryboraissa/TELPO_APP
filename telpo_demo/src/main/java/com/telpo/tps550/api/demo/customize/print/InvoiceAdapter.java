package com.telpo.tps550.api.demo.customize.print;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.softnet.devicetester.R;

import java.util.List;

public class InvoiceAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public InvoiceAdapter(Context context, List<String> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.item, parent, false);

        TextView itemText = rowView.findViewById(R.id.itemText);
        itemText.setText(values.get(position));

        return rowView;
    }
}

