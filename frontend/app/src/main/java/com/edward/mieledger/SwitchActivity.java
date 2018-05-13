package com.edward.mieledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class SwitchActivity extends AppCompatActivity {

    private static String postURL = "https://ledgermie.herokuapp.com";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private Context context;
    private ListView selectionView;
    private SelectionAdapter selectionAdapter;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        sharedPreferences = getSharedPreferences("identity", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        context = this;

        selectionView = findViewById(R.id.listView6);
        confirmButton = findViewById(R.id.button4);

        final HashMap<String, String> users = getAllUsers();
        selectionAdapter = new SelectionAdapter(this, new ArrayList<>(users.keySet()));
        selectionView.setAdapter(selectionAdapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesEditor.putString("name", selectionAdapter.checkedBoxes.get(0));
                sharedPreferencesEditor.putString("id", users.get(selectionAdapter.checkedBoxes.get(0)));
                sharedPreferencesEditor.apply();

                Intent intent = new Intent(context, LedgerActivity.class);
                context.startActivity(intent);
            }
        });

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

    private HashMap<String, String> getAllUsers() {

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

}
