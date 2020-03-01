package com.dadm.quotationshake.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dadm.quotationshake.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void onClick(View view)
    {
        switch (view.getId()) {

            case R.id.get_quotations: switchActivity(QuotationActivity.class); break;
            case R.id.favourite_quotations: switchActivity(FavouriteActivity.class); break;
            case R.id.settings: switchActivity(SettingsActivity.class); break;
            case R.id.about: switchActivity(AboutActivity.class); break;

        }
    }

    private void switchActivity(Class<? extends AppCompatActivity> activity)
    {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

}
