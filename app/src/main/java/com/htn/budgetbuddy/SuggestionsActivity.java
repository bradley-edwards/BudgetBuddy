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
import com.htn.budgetbuddy.utils.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SuggestionsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button backButton;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        tinyDB = new TinyDB(this);

        backButton = findViewById(R.id.suggestions_imageButton_leftArrow);

        backButton.setOnClickListener(this);
    }

    @Override

    private void getSuggestions() throws IOException {


        OkHttpClient client = new OkHttpClient();

        Request Request = new Request.Builder()
                .url(Constants.YELP_URL + "/businesses/search?" + "location=" + LOCATION_VAR + "&term=")
                .addHeader("Authorization", "Bearer " + Constants.YELP_KEY)
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
