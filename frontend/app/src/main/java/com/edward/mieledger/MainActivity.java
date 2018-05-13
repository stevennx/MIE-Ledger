package com.edward.mieledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.goebl.david.Webb;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String postURL = "https://ledgermie.herokuapp.com";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private Person person = null;
    private EditText editText;
    private Button button;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("identity", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        context = this;

        // uncomment to enable adding users feature
        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                person = new Person().setName(editText.getText().toString()).setID();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        registerNewUser(person);
                    }
                }).start();

                while (sharedPreferences.getBoolean("firstLaunch", true)) {

                }

                Intent intent = new Intent(context, LedgerActivity.class);
                context.startActivity(intent);
            }
        });
        */

        if (!sharedPreferences.getBoolean("firstLaunch", true)) {
            Intent intent = new Intent(this, LedgerActivity.class);
            this.startActivity(intent);
        } else {
            sharedPreferencesEditor.putString("name", "Edward");
            sharedPreferencesEditor.putString("id", "1");
            sharedPreferencesEditor.putBoolean("firstLaunch", false);
            sharedPreferencesEditor.apply();

            Intent intent = new Intent(this, LedgerActivity.class);
            this.startActivity(intent);
        }

    }

    private class Person {

        private String name = "";
        private String id = "";

        public Person() {
        }

        public String getName() {
            return this.name;
        }

        public String getID() {
            return this.id;
        }

        public Person setName(String name) {

            this.name = name;
            return this;

        }

        private String genRandomID() {

            StringBuilder currentID = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < 10; i++) {
                currentID.append(random.nextInt(10));
            }

            return currentID.toString();
        }

        public Person setID() {

            this.id = genRandomID();
            return this;

        }

        public void printPerson() {
            System.out.println(this.name);
            System.out.println(this.id);
        }

    }

    private void registerNewUser(Person person) {
        Webb webb = Webb.create();
        try {

            // comment this back in to create new users
            /*
            int idNumber = webb.post(postURL + "/users")
                    .param("action", "register")
                    .param("name", person.getName())
                    .param("password", person.getID())
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody()
                    .getInt("id");
            System.out.println("i posted");
            */

            sharedPreferencesEditor.putString("name", person.getName());
            sharedPreferencesEditor.putString("password", person.getID());
            // change "1" to "idNumber" when creating new users
            sharedPreferencesEditor.putString("id", Integer.toString(1));
            sharedPreferencesEditor.putBoolean("firstLaunch", false);
            sharedPreferencesEditor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
