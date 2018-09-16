package com.htn.budgetbuddy;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.htn.budgetbuddy.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView nameText;
    private ImageButton suggestionsImageButton, settingsImageButton, transactionsImageButton;
    private ListView progressList;

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
        progressList = findViewById(R.id.home_list_progress);

        List<String> list = new ArrayList<String>();
        list.add("Food");
        list.add("Shopping");
        list.add("Entertainment");
        list.add("Transportation");

        ProgressAdapter progressAdapter = new ProgressAdapter(this, list);

        progressList.setAdapter(progressAdapter);

        nameText.setText("Welcome, " + tinyDB.getString("givenName") + "!");

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
