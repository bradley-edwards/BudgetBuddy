package com.htn.budgetbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.htn.budgetbuddy.utils.TinyDB;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText foodText, shoppingText, entertainmentText, transportationText;
    private Button saveButton;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tinyDB = new TinyDB(this);

        foodText = findViewById(R.id.settings_edit_food);
        shoppingText = findViewById(R.id.settings_edit_shopping);
        entertainmentText = findViewById(R.id.settings_edit_entertainment);
        transportationText = findViewById(R.id.settings_edit_transportation);
        saveButton = findViewById(R.id.settings_button_save);

        foodText.setText(tinyDB.getDouble("foodBudget", 0) + "");
        shoppingText.setText(tinyDB.getDouble("shoppingBudget", 0) + "");
        entertainmentText.setText(tinyDB.getDouble("entertainmentBudget", 0) + "");
        transportationText.setText(tinyDB.getDouble("transportationBudget", 0) + "");

        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_button_save:
                if (foodText.getText().toString().isEmpty() || shoppingText.getText().toString().isEmpty() ||
                        entertainmentText.getText().toString().isEmpty() || transportationText.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }
                tinyDB.putDouble("foodBudget", Double.parseDouble(foodText.getText().toString()));
                tinyDB.putDouble("shoppingBudget", Double.parseDouble(shoppingText.getText().toString()));
                tinyDB.putDouble("entertainmentBudget", Double.parseDouble(entertainmentText.getText().toString()));
                tinyDB.putDouble("transportationBudget", Double.parseDouble(transportationText.getText().toString()));
                Toast.makeText(this, "Budget values saved", Toast.LENGTH_SHORT).show();
                openHomeActivity();
                break;
        }
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
