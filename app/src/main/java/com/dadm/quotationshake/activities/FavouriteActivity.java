package com.dadm.quotationshake.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dadm.quotationshake.R;
import com.dadm.quotationshake.tasks.FetchQuotationTask;
import com.dadm.quotationshake.database.QuotationContract;
import com.dadm.quotationshake.database.QuotationRoom;
import com.dadm.quotationshake.database.QuotationSQLiteOpenHelper;
import com.dadm.quotationshake.database.Quotation;

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
        new FetchQuotationTask(this).execute(preferedDatabase);
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

    public void onQuotationsLoaded(List<Quotation> quotations)
    {
        // Si no se ha recibido ninguna cita, oculta la opción de borrar las citas favoritas.
        if (quotations == null || quotations.size() == 0) clearFavouritesItem.setVisible(false);
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

    private void deleteQuotation(final Quotation quotation)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (preferedDatabase)
                {
                    case SQLite:
                        QuotationSQLiteOpenHelper.getInstance(FavouriteActivity.this).deleteQuotation(quotation);
                        break;

                    case ROOM:
                        QuotationRoom.getInstance(FavouriteActivity.this).quotationDao().delete(quotation);
                        break;
                }
            }
        }).start();
    }

    private void deleteQuotations()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (preferedDatabase)
                {
                    case SQLite:
                        QuotationSQLiteOpenHelper.getInstance(FavouriteActivity.this).deleteAllQuotations();
                        break;

                    case ROOM:
                        QuotationRoom.getInstance(FavouriteActivity.this).quotationDao().deleteAll();
                        break;
                }
            }
        }).start();

    }

    private String getSearchUrl(String authorName)
    {
        return WIKI_URL + "?search=" + authorName;
    }
}
