package com.htn.smartspend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.htn.smartspend.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginButton;
    private EditText usernameEdit;

    private SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT);
                } else {
                    try {
                        getCustomerInfo(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    openMainActivity();
                }
                break;
        }
    }

    private void getCustomerInfo(String username) throws IOException {

        String customerId = Constants.NAME_CUSTOMERID_MAP.get(username);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/customers/" + customerId)
                .addHeader("Authorization", Constants.API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();
        try {
            JSONObject json = new JSONObject(result);

            prefs.edit().putString("givenName", json.getString("givenName")).apply();
            prefs.edit().putString("maidenName", json.getString("maidenName")).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
