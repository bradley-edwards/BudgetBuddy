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
import com.htn.budgetbuddy.utils.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginActivity context;

    private Button loginButton;
    private EditText usernameEdit;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        tinyDB = new TinyDB(this);

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
                            tinyDB.putString("customerId", customerId);
                            tinyDB.putString("customerId", customerId);
                            tinyDB.putString("givenName", json.has("givenName") ? json.getString("givenName") : "");
                            tinyDB.putString("surName", json.has("surName") ? json.getString("surName") : "");
                            tinyDB.putString("totalIncome", json.has("totalIncome") ? json.getString("totalIncome") : "");

                            if (json.has("maskedRelatedBankAccounts") &&
                                    json.getJSONObject("maskedRelatedBankAccounts").has("individual")) {
                                JSONArray bankAccounts = json.getJSONObject("maskedRelatedBankAccounts").getJSONArray("individual");
                                ArrayList<String> bankAccountIds = new ArrayList<>();
                                for (int i = 0; i < bankAccounts.length(); ++i) {
                                    bankAccountIds.add(bankAccounts.getJSONObject(i).getString("accountId"));
                                }

                                if (!bankAccountIds.isEmpty()) {
                                    tinyDB.putListString("bankAccounts", bankAccountIds);
                                }

                            }
                            if (json.has("maskedRelatedCreditCardAccounts") &&
                                    json.getJSONObject("maskedRelatedCreditCardAccounts").has("individual")) {
                                JSONArray creditCardAccounts = json.getJSONObject("maskedRelatedCreditCardAccounts").getJSONArray("authorized");
                                ArrayList<String> creditCardAccountIds = new ArrayList<>();
                                for (int i = 0; i < creditCardAccounts.length(); ++i) {
                                    creditCardAccountIds.add(creditCardAccounts.getJSONObject(i).getString("accountId"));
                                }
                                if (!creditCardAccountIds.isEmpty()) {
                                    tinyDB.putListString("creditCardAccounts", creditCardAccountIds);
                                }
                            }

                            getTransaction(customerId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void getTransaction(String customerId) {

        OkHttpClient client = new OkHttpClient();

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
                                ArrayList<Transaction> transactions = new ArrayList<>();
                                ArrayList<Transaction> monthTransactions = new ArrayList<>();
                                ArrayList<String> usedNames = new ArrayList<>();

                                for (int i = 0; i < foundTransactions.length(); ++i) {
                                    JSONObject tran = foundTransactions.getJSONObject(i);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    Date tranDate = null;

                                    if (usedNames.contains(tran.getString("merchantName"))) {
                                        continue;
                                    }

                                    usedNames.add(tran.getString("merchantName"));

                                    try {
                                        String strDate = tran.getString("originationDateTime");
                                        if (strDate.length() == 20) {
                                            tranDate = dateFormat.parse(tran.getString("originationDateTime"));
                                        } else {
                                            tranDate = dateFormat2.parse(tran.getString("originationDateTime"));
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Date date = new Date();
                                    int days = daysBetween(date, tranDate);

                                    if (days <= 10) {
                                        transactions.add(gson.fromJson(foundTransactions.getJSONObject(i).toString(), Transaction.class));
                                    }

                                    if (sameMonth(date, tranDate)) {
                                        monthTransactions.add(gson.fromJson(foundTransactions.getJSONObject(i).toString(), Transaction.class));
                                    }
                                }
                                tinyDB.putListTransactions("transactions", transactions);
                                tinyDB.putListTransactions("monthTransactions", monthTransactions);
                                getSpending();
                            } else {
                            }
                            openHomeActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public boolean sameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        return sameDay;
    }

    public int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void getSpending() {
        ArrayList<Transaction> monthTrans = tinyDB.getListTransaction("monthTransactions");
        double entSpent = 0;
        double shopSpent = 0;
        double transSpent = 0;
        double foodSpent = 0;
        int arrLen = monthTrans.size();
        for (int i = 0; i < arrLen; i++) {
            String cat = monthTrans.get(i).getCategoryTags().get(0);
            double amount = monthTrans.get(i).getCurrencyAmount();
            if (cat.equalsIgnoreCase("Entertainment")) {
                entSpent += amount;
            } else if (cat.equalsIgnoreCase("Home")) {
                shopSpent += amount;
            } else if (cat.equalsIgnoreCase("Food and Dining")) {
                foodSpent -= amount;
            } else if (cat.equalsIgnoreCase("Auto and Transport")) {
                transSpent += amount;
            }
        }
        tinyDB.putDouble("Entertainment", entSpent);
        tinyDB.putDouble("Home", shopSpent);
        tinyDB.putDouble("Food and Dining", foodSpent);
        tinyDB.putDouble("Auto and Transport", transSpent);
    }
}