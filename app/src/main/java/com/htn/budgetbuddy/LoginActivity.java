package com.htn.budgetbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.htn.budgetbuddy.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginActivity context;

    private Button loginButton;
    private EditText usernameEdit;

    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");

        loginButton = findViewById(R.id.login_button_login);
        usernameEdit = findViewById(R.id.login_edit_username);

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button_login:
                String username = usernameEdit.getText().toString();
                if (username.isEmpty()) {
                    Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        getCustomerInfo(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void getCustomerInfo(String username) throws IOException {

        if (!Constants.NAME_CUSTOMERID_MAP.containsKey(username)) {
            Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        final String customerId = Constants.NAME_CUSTOMERID_MAP.get(username);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/customers/" + customerId)
                .addHeader("Authorization", Constants.API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String result = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(result);

                            if (json.getString("errorMsg") != null && !json.getString("errorMsg").isEmpty()) {
                                Toast.makeText(context, json.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            userRef.child("customerId").setValue(customerId);
                            userRef.child("givenName").setValue(json.getString("givenName"));
                            userRef.child("maidenName").setValue(json.getString("maidenName"));
                            userRef.child("totalIncome").setValue(json.getString("totalIncome"));
                            userRef.child("bankAccounts").setValue(json.getJSONObject("maskedRelatedBankAccounts").getJSONArray("individual"));
                            userRef.child("creditCardAccounts").setValue(json.getJSONObject("maskedRelatedCreditCardAccounts").getJSONArray("authorized"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        openMainActivity();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
