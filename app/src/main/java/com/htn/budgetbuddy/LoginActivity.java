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
import com.google.gson.Gson;
import com.htn.budgetbuddy.models.Transaction;
import com.htn.budgetbuddy.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        userRef = database.getReference("user/" + username);

        final String customerId = Constants.NAME_CUSTOMERID_MAP.get(username);

        OkHttpClient client = new OkHttpClient();

        Request customerRequest = new Request.Builder()
                .url(Constants.BASE_URL + "/customers/" + customerId)
                .addHeader("Authorization", Constants.API_KEY)
                .build();

        client.newCall(customerRequest).enqueue(new Callback() {
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
                            JSONObject json = new JSONObject(result).getJSONObject("result");

                            if (json.has("errorMsg") &&
                                    !json.getString("errorMsg").equalsIgnoreCase("null")) {
                                Toast.makeText(context, json.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            userRef.child("customerId").setValue(customerId);
                            userRef.child("givenName").setValue(json.has("givenName") ? json.getString("givenName") : "");
                            userRef.child("maidenName").setValue(json.has("maidenName") ? json.getString("maidenName") : "");
                            userRef.child("totalIncome").setValue(json.has("totalIncome") ? json.getString("totalIncome") : "");
                            if (json.has("maskedRelatedBankAccounts") &&
                                    json.getJSONObject("maskedRelatedBankAccounts").has("individual")) {
                                JSONArray bankAccounts = json.getJSONObject("maskedRelatedBankAccounts").getJSONArray("individual");
                                List<String> bankAccountIds = new ArrayList<>();
                                for (int i = 0; i < bankAccounts.length(); ++i) {
                                    bankAccountIds.add(bankAccounts.getJSONObject(i).getString("accountId"));
                                }
                                userRef.child("bankAccounts").setValue(bankAccountIds);
                            } else {
                                userRef.child("bankAccounts").setValue(null);
                            }
                            if (json.has("maskedRelatedCreditCardAccounts") &&
                                    json.getJSONObject("maskedRelatedCreditCardAccounts").has("individual")) {
                                JSONArray bankAccounts = json.getJSONObject("maskedRelatedCreditCardAccounts").getJSONArray("authorized");
                                List<String> bankAccountIds = new ArrayList<>();
                                for (int i = 0; i < bankAccounts.length(); ++i) {
                                    bankAccountIds.add(bankAccounts.getJSONObject(i).getString("accountId"));
                                }
                                userRef.child("creditCardAccounts").setValue(bankAccountIds);
                            } else {
                                userRef.child("creditCardAccounts").setValue(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        final Request transactionRequest = new Request.Builder()
                .url(Constants.BASE_URL + "/customers/" + customerId + "/transactions")
                .addHeader("Authorization", Constants.API_KEY)
                .build();

        client.newCall(transactionRequest).enqueue(new Callback() {
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

                            if (json.has("errorMsg") &&
                                    !json.getString("errorMsg").equalsIgnoreCase("null")) {
                                Toast.makeText(context, json.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (json.has("result")) {
                                JSONArray foundTransactions = json.getJSONArray("result");
                                Gson gson = new Gson();
                                List<Transaction> transactions = new ArrayList<>();
                                for (int i = 0; i < foundTransactions.length(); ++i) {
                                    transactions.add(gson.fromJson(foundTransactions.getJSONObject(i).toString(), Transaction.class));
                                }
                                userRef.child("transactions").setValue(transactions);
                            } else {
                                userRef.child("transactions").setValue(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                openMainActivity();
            }
        });

        openMainActivity();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
