package com.edward.mieledger;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.goebl.david.Webb;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayAdapter extends ArrayAdapter {

    private String postURL = "https://ledgermie.herokuapp.com";

    private Activity context;
    private ArrayList<Map.Entry<String, Integer>> transactionArray;
    private HashMap<String, String> users;
    private String type;
    private String currentUser;

    public DisplayAdapter(Activity context, ArrayList<Map.Entry<String, Integer>> transactionArray, HashMap<String, String> users, String type, String currentUser) {
        super(context, R.layout.listview_display, transactionArray);

        this.context = context;
        this.transactionArray = transactionArray;
        this.users = users;
        this.type = type;
        this.currentUser = currentUser;
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
        Button paidButton = rowView.findViewById(R.id.button10);
        nameText.setText(transactionArray.get(position).getKey());
        amountText.setText(centsToDollars(transactionArray.get(position).getValue()));
        paidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("lenders")) {
                    addTransactionAndRefresh(users.get(currentUser), users.get(transactionArray.get(position).getKey()), -transactionArray.get(position).getValue());
                    context.startActivity(new Intent(context, LedgerActivity.class));
                } else if (type.equals("borrowers")) {
                    addTransactionAndRefresh(users.get(transactionArray.get(position).getKey()), users.get(currentUser), -transactionArray.get(position).getValue());
                    context.startActivity(new Intent(context, LedgerActivity.class));
                }
            }
        });

        return rowView;
    }

    private void addTransactionAndRefresh(final String borrowerID, final String lenderID, final int amount) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Webb webb = Webb.create();
                JSONObject jsonObject = webb.post(postURL + "/users/" + borrowerID + "/owe")
                        .param("action", "loan")
                        .param("borrower_id", borrowerID)
                        .param("lender_id", lenderID)
                        .param("amount", amount)
                        .param("description", "payment of outstanding loans")
                        .param("active", true)
                        .ensureSuccess()
                        .asJsonObject()
                        .getBody();
            }
        }).start();

    }

}
