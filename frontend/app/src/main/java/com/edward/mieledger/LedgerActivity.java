package com.edward.mieledger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.goebl.david.Webb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LedgerActivity extends AppCompatActivity {

    private String postURL = "https://ledgermie.herokuapp.com";

    private SharedPreferences sharedPreferences;
    private TextView nameText;
    private Button addTransactionButton;
    private Button switchUserButton;
    private Button historyButton;
    private ListView lenderList;
    private ListView borrowerList;
    private DisplayAdapter lenderDisplayAdapter;
    private DisplayAdapter borrowerDisplayAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger);
        sharedPreferences = getSharedPreferences("identity", Context.MODE_PRIVATE);
        context = this;
        System.out.println("ledger activity started");

        lenderList = findViewById(R.id.listView4);
        borrowerList = findViewById(R.id.listView3);
        nameText = findViewById(R.id.textView4);
        nameText.setText(sharedPreferences.getString("name", "Edward"));

        GetTransactionsRunnable getTransactionsRunnable = new GetTransactionsRunnable();
        new Thread(getTransactionsRunnable).start();
        while (!getTransactionsRunnable.isCompleted()) {

        }
        System.out.println("point a");
        ArrayList<HashMap<String, Integer>> transactions = getTransactionsRunnable.getTransactions();
        ArrayList<Map.Entry<String, Integer>> lenderArrayList = new ArrayList<>(transactions.get(0).entrySet());
        ArrayList<Map.Entry<String, Integer>> borrowerArrayList = new ArrayList<>(transactions.get(1).entrySet());
        System.out.println("point b");

        lenderDisplayAdapter = new DisplayAdapter((Activity) context, lenderArrayList);
        borrowerDisplayAdapter = new DisplayAdapter((Activity) context, borrowerArrayList);
        lenderList.setAdapter(lenderDisplayAdapter);
        borrowerList.setAdapter(borrowerDisplayAdapter);
        System.out.println("point c");

        addTransactionButton = findViewById(R.id.button3);
        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                context.startActivity(intent);
            }
        });

        switchUserButton = findViewById(R.id.button5);
        switchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SwitchActivity.class);
                context.startActivity(intent);
            }
        });

        historyButton = findViewById(R.id.button6);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HistoryActivity.class);
                context.startActivity(intent);
            }
        });
        System.out.println("point d");
    }

    private class GetTransactionsRunnable implements Runnable {

        private volatile ArrayList<HashMap<String, Integer>> transactions;
        private volatile boolean completed = false;

        @Override
        public void run() {
            Webb webb = Webb.create();
            JSONObject jsonObject = webb.get(postURL + "/users/" + sharedPreferences.getString("id", "1") + "/summary")
                    .param("action", "load")
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody();
            transactions = parseUserLedger(jsonObject);
            completed = true;
        }

        public boolean isCompleted() {
            return completed;
        }

        public ArrayList<HashMap<String, Integer>> getTransactions() {
            return transactions;
        }

    }

    private ArrayList<HashMap<String, Integer>> parseUserLedger(JSONObject userLedger) {

        HashMap<String, Integer> userBorrowings = new HashMap<>();
        HashMap<String, Integer> userLendings = new HashMap<>();

        try {
            Iterator userInteractions = userLedger.keys();
            while (userInteractions.hasNext()) {
                String other = (String) userInteractions.next();
                int transactionAmount = userLedger.getInt(other);
                if (transactionAmount > 0) {
                    userBorrowings.put(other, transactionAmount);
                } else if (transactionAmount < 0) {
                    userLendings.put(other, -transactionAmount);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<HashMap<String, Integer>> userTransactions = new ArrayList<>();
        userTransactions.add(userBorrowings);
        userTransactions.add(userLendings);
        System.out.println(userTransactions.toString());
        return userTransactions;
    }

    private JSONObject loadUserLedger(String name, String password) {
        Webb webb = Webb.create();
        JSONObject jsonObject = webb.get(postURL + "/users/" + sharedPreferences.getString("id", "1") + "/summary")
                .param("action", "load")
                .param("name", name)
                .param("password", password)
                .ensureSuccess()
                .asJsonObject()
                .getBody();
        System.out.println(sharedPreferences.getString("id", "1"));
        System.out.println(jsonObject.toString());
        return jsonObject;
    }

}
