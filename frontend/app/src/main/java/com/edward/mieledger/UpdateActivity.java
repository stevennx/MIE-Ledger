package com.edward.mieledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateActivity extends AppCompatActivity {

    private static String postURL = "https://ledgermie.herokuapp.com";
    private ListView borrowerList;
    private RadioButton owesButton;
    private RadioButton paidButton;
    private EditText amountText;
    private ListView lenderList;
    private EditText descriptionText;
    private Button confirmButton;
    private Context context;
    private static SharedPreferences sharedPreferences;
    private SelectionAdapter borrowerSelectionAdapter;
    private SelectionAdapter lenderSelectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        context = this;
        sharedPreferences = getSharedPreferences("identity", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        final HashMap<String, String> users = (HashMap<String, String>) intent.getSerializableExtra("users");
        System.out.println("update activity started");

        borrowerList = findViewById(R.id.listView2);
        owesButton = findViewById(R.id.radioButton);
        paidButton = findViewById(R.id.radioButton2);
        amountText = findViewById(R.id.editText2);
        lenderList = findViewById(R.id.listView);
        descriptionText = findViewById(R.id.editText3);
        confirmButton = findViewById(R.id.button2);

        borrowerSelectionAdapter = new SelectionAdapter(this, new ArrayList<>(users.keySet()));
        borrowerList.setAdapter(borrowerSelectionAdapter);
        lenderSelectionAdapter = new SelectionAdapter(this, new ArrayList<>(users.keySet()));
        lenderList.setAdapter(lenderSelectionAdapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println(borrowerSelectionAdapter.checkedBoxes.toString());
                System.out.println(lenderSelectionAdapter.checkedBoxes.toString());

                if (!isProperAmount(amountText.getText().toString())) {
                    Toast.makeText(context, "Please enter a proper amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (borrowerSelectionAdapter.checkedBoxes.size() == 0) {
                    Toast.makeText(context, "Please select at least one borrower", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lenderSelectionAdapter.checkedBoxes.size() == 0) {
                    Toast.makeText(context, "Please select at least one lender", Toast.LENGTH_SHORT).show();
                    return;
                }

                final int sign;
                if (owesButton.isChecked()) {
                    sign = 1;
                } else if (paidButton.isChecked()) {
                    sign = -1;
                } else {
                    Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (final String borrower : borrowerSelectionAdapter.checkedBoxes) {
                    for (final String lender : lenderSelectionAdapter.checkedBoxes) {
                        addTransaction(sharedPreferences.getString("name", ""), sharedPreferences.getString("password", ""), users.get(borrower), users.get(lender), sign * stringAmountToCents(amountText.getText().toString()), descriptionText.getText().toString());
                    }
                }

                Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LedgerActivity.class);
                context.startActivity(intent);
            }
        });

    }

    /*
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
    */

    private static boolean isProperAmount(String amount) {

        boolean decimalPointSeen = false;

        int length = amount.length();
        if (length == 0) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            char c = amount.charAt(i);
            if (c == '.') {
                if (decimalPointSeen) {
                    return false;
                } else {
                    decimalPointSeen = true;
                }
            } else if (!((c == '0') || (c == '1') || (c == '2') || (c == '3') || (c == '4') || (c == '5') || (c == '6') || (c == '7') || (c == '8') || (c == '9'))) {
                return false;
            }
        }

        return true;

    }

    private static int stringAmountToCents(String amount) {
        return (int) (100f * Float.valueOf(amount));
    }

    private static void addTransaction(final String name, final String password, final String borrowerID, final String lenderID, final int amount, final String description) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Webb webb = Webb.create();
                JSONObject jsonObject = webb.post(postURL + "/users/" + borrowerID + "/owe")
                        .param("action", "loan")
                        .param("name", name)
                        .param("password", password)
                        .param("borrower_id", borrowerID)
                        .param("lender_id", lenderID)
                        .param("amount", amount)
                        .param("description", description)
                        .param("active", true)
                        .ensureSuccess()
                        .asJsonObject()
                        .getBody();
            }
        }).start();

    }
}

