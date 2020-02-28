package com.dadm.quotationshake.view.activities;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.dadm.quotationshake.R;

public class FavouriteActivity extends AppCompatActivity {

    private static final String WIKI_URL = "https://en.wikipedia.org/wiki/Special:Search";

    private String authorName = "Albert Einstein";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
    }

    public void onClick(View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getSearchUrl(authorName)));
        startActivity(intent);
    }

    public String getSearchUrl(String authorName)
    {
        return WIKI_URL + "?search=" + authorName;
    }
}
