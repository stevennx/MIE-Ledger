package com.edward.mieledger;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private String postURL = "https://ledgermie.herokuapp.com";
    ListView historyList;
    Button returnButton;
    TransactionAdapter transactionAdapter;
    GetUsersAndTransactionsRunnable getUsersRunnable;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        context = this;

        getUsersRunnable = new GetUsersAndTransactionsRunnable();
        new Thread(getUsersRunnable).start();
        while (!getUsersRunnable.usersCompleted()) {

        }
        HashMap<String, String> usersById = getAllUsersById();
        while (!getUsersRunnable.transactionsCompleted()) {

        }
        ArrayList<JSONObject> transactions = getTransactions();

        historyList = findViewById(R.id.listView6);
        transactionAdapter = new TransactionAdapter(this, transactions, usersById);
        historyList.setAdapter(transactionAdapter);

        returnButton = findViewById(R.id.button9);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Map.Entry<Integer, Boolean>> changedTransactions = new ArrayList<>(transactionAdapter.changedTransactions.entrySet());
                int length = changedTransactions.size();
                for (int i = 0; i < length; i++) {
                    updateTransaction(changedTransactions.get(i).getKey(), changedTransactions.get(i).getValue());
                }
                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LedgerActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private class GetUsersAndTransactionsRunnable implements Runnable {

        private volatile JSONArray userArray;
        private volatile JSONArray transactionArray;
        private volatile boolean usersCompleted = false;
        private volatile boolean transactionsCompleted = false;

        @Override
        public void run() {
            Webb webb = Webb.create();
            userArray = webb.get(postURL + "/users")
                    .ensureSuccess()
                    .asJsonArray()
                    .getBody();
            usersCompleted = true;
            transactionArray = webb.get(postURL + "/transactions")
                    .ensureSuccess()
                    .asJsonArray()
                    .getBody();
            transactionsCompleted = true;
        }

        public boolean usersCompleted() {
            return usersCompleted;
        }

        public JSONArray getUserData() {
            return userArray;
        }

        public boolean transactionsCompleted() {
            return transactionsCompleted;
        }

        public JSONArray getTransactionData() {
            return transactionArray;
        }

    }

    private HashMap<String, String> getAllUsersById() {

        JSONArray userData = getUsersRunnable.getUserData();

        int length = userData.length();
        HashMap<String, String> users = new HashMap<>();

        try {
            for (int i = 0; i < length; i++) {
                users.put(userData.getJSONObject(i).getString("id"), userData.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return users;
    }

    private ArrayList<JSONObject> getTransactions() {

        JSONArray transactionData = getUsersRunnable.getTransactionData();

        int length = transactionData.length();
        ArrayList<JSONObject> transactions = new ArrayList<>();

        try {
            for (int i = 0; i < length; i++) {
                transactions.add(transactionData.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    private void updateTransaction(final int transactionID, final boolean active) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Webb webb = Webb.create();
                JSONObject jsonObject = webb.post(postURL + "/transactions/active")
                        .param("transaction_id", transactionID)
                        .param("active", active)
                        .ensureSuccess()
                        .asJsonObject()
                        .getBody();
                System.out.println(jsonObject.toString());
            }
        }).start();

    }

}























