package com.edward.mieledger;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class DisplayAdapter extends ArrayAdapter {

    private Activity context;
    private ArrayList<Map.Entry<String, Integer>> transactionArray;

    public DisplayAdapter(Activity context, ArrayList<Map.Entry<String, Integer>> transactionArray) {
        super(context, R.layout.listview_display, transactionArray);

        this.context = context;
        this.transactionArray = transactionArray;
    }

    private String formatTwoDigits(int myInt) {
        if ((0 <= myInt) && (myInt <= 9)) {
            return "0" + Integer.toString(myInt);
        } else {
            return Integer.toString(myInt % 100);
        }
    }

    private String centsToDollars(int cents) {
        if ((0 <= cents) && (cents <= 99)) {
            return "0." + formatTwoDigits(cents);
        } else {
            return Integer.toString(cents / 100) + "." + formatTwoDigits(cents % 100);
        }
    }

    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_display, null);

        TextView nameText = rowView.findViewById(R.id.textView2);
        TextView amountText = rowView.findViewById(R.id.textView3);
        nameText.setText(transactionArray.get(position).getKey());
        amountText.setText(centsToDollars(transactionArray.get(position).getValue()));

        return rowView;
    }

}
