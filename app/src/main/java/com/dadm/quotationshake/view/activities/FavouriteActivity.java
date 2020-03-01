package com.dadm.quotationshake.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dadm.quotationshake.R;
import com.dadm.quotationshake.model.database.QuotationContract;
import com.dadm.quotationshake.model.database.QuotationRoom;
import com.dadm.quotationshake.model.database.QuotationSQLiteOpenHelper;
import com.dadm.quotationshake.model.entity.Quotation;

import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private static final String WIKI_URL = "https://en.wikipedia.org/wiki/Special:Search";

    private static QuotationContract.Database preferedDatabase;


    private MenuItem clearFavouritesItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        preferedDatabase = QuotationContract.getPreferredDatabase(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Guarda una referencia de los items del menú.
        clearFavouritesItem = menu.getItem(0);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // Si la opción es para borrar todas las citas favoritas.
        if (item.equals(clearFavouritesItem)) deleteQuotations();

        // Si la opción es para
        else if (false) ;

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view)
    {
        searchForAuthor("Albert Einstein");
    }

    private void searchForAuthor(String authorName)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getSearchUrl(authorName)));
        startActivity(intent);
    }

    private List<Quotation> getQuotations()
    {
        switch (preferedDatabase)
        {
            case SQLite:
                return QuotationSQLiteOpenHelper.getInstance(this).getQuotations();

            case ROOM:
                return QuotationRoom.getInstance(this).quotationDao().getAll();

            default: return null;
        }
    }

    private void deleteQuotation(Quotation quotation)
    {
        switch (preferedDatabase)
        {
            case SQLite:
                QuotationSQLiteOpenHelper.getInstance(this).deleteQuotation(quotation);
                break;

            case ROOM:
                QuotationRoom.getInstance(this).quotationDao().delete(quotation);
                break;
        }
    }

    private void deleteQuotations()
    {
        switch (preferedDatabase)
        {
            case SQLite:
                QuotationSQLiteOpenHelper.getInstance(this).deleteAllQuotations();
                break;

            case ROOM:
                QuotationRoom.getInstance(this).quotationDao().deleteAll();
                break;
        }
    }

    private String getSearchUrl(String authorName)
    {
        return WIKI_URL + "?search=" + authorName;
    }
}
