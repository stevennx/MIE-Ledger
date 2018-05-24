package com.edward.mieledger;

import android.content.Context;
import android.content.SharedPreferences;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class LedgerConstants {

    public String postURL = "https://ledgermie.herokuapp.com";
    SharedPreferences sharedPreferences;

    public LedgerConstants(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    private class GetUsersRunnable implements Runnable {

        private volatile JSONArray jsonArray;
        private volatile boolean completed = false;

        @Override
        public void run() {
            Webb webb = Webb.create();
            jsonArray = webb.get(postURL + "/users")
                    .ensureSuccess()
                    .asJsonArray()
                    .getBody();
            completed = true;
        }

        public boolean isCompleted() {
            return completed;
        }

        public JSONArray getUserData() {
            return jsonArray;
        }

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

    public HashMap<String, String> getAllUsersById() {

        GetUsersAndTransactionsRunnable getUsersAndTransactionsRunnable = new GetUsersAndTransactionsRunnable();
        new Thread(getUsersAndTransactionsRunnable).start();
        while (!getUsersAndTransactionsRunnable.usersCompleted()) {

        }
        JSONArray userData = getUsersAndTransactionsRunnable.getUserData();

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

    public HashMap<String, String> getAllUsers() {

        GetUsersRunnable getUsersRunnable = new GetUsersRunnable();
        new Thread(getUsersRunnable).start();
        while (!getUsersRunnable.isCompleted()) {

        }
        JSONArray userData = getUsersRunnable.getUserData();

        int length = userData.length();
        HashMap<String, String> users = new HashMap<>();

        try {
            for (int i = 0; i < length; i++) {
                users.put(userData.getJSONObject(i).getString("name"), userData.getJSONObject(i).getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return users;
    }

    public ArrayList<HashMap<String, Integer>> getUserTransactions() {

        GetTransactionsRunnable getTransactionsRunnable = new GetTransactionsRunnable();
        new Thread(getTransactionsRunnable).start();
        while (!getTransactionsRunnable.isCompleted()) {

        }
        return getTransactionsRunnable.getTransactions();

    }

    public ArrayList<JSONObject> getAllTransactions() {

        GetUsersAndTransactionsRunnable getUsersAndTransactionsRunnable = new GetUsersAndTransactionsRunnable();
        new Thread(getUsersAndTransactionsRunnable).start();
        while (!getUsersAndTransactionsRunnable.transactionsCompleted()) {

        }
        JSONArray transactionData = getUsersAndTransactionsRunnable.getTransactionData();

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

}
