package com.htn.budgetbuddy;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView nameText;
    private ImageButton suggestionsImageButton, settingsImageButton, transactionsImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nameText = findViewById(R.id.home_text_name);
        suggestionsImageButton = findViewById(R.id.home_imageButton_suggestions);
        settingsImageButton = findViewById(R.id.home_imageButton_settings);
        transactionsImageButton = findViewById(R.id.home_imageButton_transactions);

        nameText.setText("");

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
                break;
            case R.id.home_imageButton_transactions:
                break;
        }
    }

}
