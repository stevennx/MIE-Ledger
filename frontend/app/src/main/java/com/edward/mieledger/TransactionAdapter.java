package com.edward.mieledger;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionAdapter extends ArrayAdapter {

    private Activity context;
    private ArrayList<JSONObject> transactionArray;
    private ArrayList<Boolean> activeArray;
    public HashMap<Integer, Boolean> changedTransactions;
    private HashMap<String, String> usersById;

    public TransactionAdapter(Activity context, ArrayList<JSONObject> transactionArray, HashMap<String, String> usersById) {
        super(context, R.layout.listview_transaction, transactionArray);
        System.out.println("adapter created");

        this.context = context;
        this.transactionArray = transactionArray;
        this.usersById = usersById;

        int length = transactionArray.size();
        this.activeArray = new ArrayList<>();
        try {
            for (int i = 0; i < length; i++) {
                activeArray.add(transactionArray.get(i).getBoolean("active"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.changedTransactions = new HashMap<>();
    }

    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_transaction, null);

        TextView borrowerAndLenderTextView = rowView.findViewById(R.id.textView5);
        TextView descriptionTextView = rowView.findViewById(R.id.textView6);
        TextView amountTextView = rowView.findViewById(R.id.textView7);
        Button cancelButton = rowView.findViewById(R.id.button7);
        Button restoreButton = rowView.findViewById(R.id.button8);

        JSONObject transaction = transactionArray.get(position);
        int amount = 0;
        boolean active = activeArray.get(position);
        int id = 0;
        try {
            amount = transaction.getInt("amount");
            id = transaction.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (amount < 0) {
            try {
                borrowerAndLenderTextView.setText(usersById.get(transaction.getString("borrower_id")) + " paid " + usersById.get(transaction.getString("lender_id")));
                descriptionTextView.setText(transaction.getString("description"));
                amountTextView.setText(Integer.toString(-amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (amount > 0) {
            try {
                borrowerAndLenderTextView.setText(usersById.get(transaction.getString("borrower_id")) + " borrowed from " + usersById.get(transaction.getString("lender_id")));
                descriptionTextView.setText(transaction.getString("description"));
                amountTextView.setText(Integer.toString(amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setClickListeners(active, cancelButton, restoreButton, id, position);

        return rowView;

    }

    private void setClickListeners(final boolean active, final Button cancelButton, final Button restoreButton, final int transactionID, final int position) {
        if (active) {

            cancelButton.setBackgroundColor(Color.GRAY);
            cancelButton.setTextColor(Color.BLACK);
            restoreButton.setBackgroundColor(Color.WHITE);
            restoreButton.setTextColor(Color.WHITE);
            restoreButton.setOnClickListener(null);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changedTransactions.put(transactionID, !active);
                    activeArray.set(position, !active);
                    setClickListeners(!active, cancelButton, restoreButton, transactionID, position);
                }
            });

        } else {

            restoreButton.setBackgroundColor(Color.GRAY);
            restoreButton.setTextColor(Color.BLACK);
            cancelButton.setBackgroundColor(Color.WHITE);
            cancelButton.setTextColor(Color.WHITE);
            cancelButton.setOnClickListener(null);
            restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changedTransactions.put(transactionID, !active);
                    activeArray.set(position, !active);
                    setClickListeners(!active, cancelButton, restoreButton, transactionID, position);
                }
            });

        }
    }


}





