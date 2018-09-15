package com.htn.budgetbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.htn.budgetbuddy.models.Transaction;
import com.htn.budgetbuddy.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SuggestionsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button backButton;

    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        context = this;

        database = FirebaseDatabase.getInstance();
    }

    @Override

    private void getCustomerInfo(String username) throws IOException {

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
