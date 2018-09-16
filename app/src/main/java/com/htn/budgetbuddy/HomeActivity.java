package com.htn.budgetbuddy;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.htn.budgetbuddy.utils.TinyDB;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView nameText;
    private ImageButton suggestionsImageButton, settingsImageButton, transactionsImageButton;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tinyDB = new TinyDB(this);

        nameText = findViewById(R.id.home_text_name);
        suggestionsImageButton = findViewById(R.id.home_imageButton_suggestions);
        settingsImageButton = findViewById(R.id.home_imageButton_settings);
        transactionsImageButton = findViewById(R.id.home_imageButton_transactions);

        nameText.setText("Welcome, " + tinyDB.getString("givenName"));

        suggestionsImageButton.setOnClickListener(this);
        settingsImageButton.setOnClickListener(this);
        transactionsImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_imageButton_suggestions:
                break;
            case R.id.home_imageButton_settings:
                openSettingsActivity();
                break;
            case R.id.home_imageButton_transactions:
                break;
        }
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
