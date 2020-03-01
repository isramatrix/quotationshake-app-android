package com.dadm.quotationshake.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dadm.quotationshake.R;
import com.dadm.quotationshake.tasks.ModelViewAdapter;
import com.dadm.quotationshake.tasks.FetchQuotationTask;
import com.dadm.quotationshake.database.QuotationContract;
import com.dadm.quotationshake.database.QuotationRoom;
import com.dadm.quotationshake.database.QuotationSQLiteOpenHelper;
import com.dadm.quotationshake.database.Quotation;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity implements ModelViewAdapter.OnItemClickListener, ModelViewAdapter.OnItemLongClickListener {

    private static final String WIKI_URL = "https://en.wikipedia.org/wiki/Special:Search";

    private static QuotationContract.Database preferedDatabase;
    private ModelViewAdapter adapter;

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
        getMenuInflater().inflate(R.menu.favourite_activity_menu, menu);

        clearFavouritesItem = menu.findItem(R.id.clearAllFavsMenuItem);
        clearFavouritesItem.setVisible(false);

        new FetchQuotationTask(this).execute(preferedDatabase);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()){
            case R.id.clearAllFavsMenuItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(android.R.drawable.stat_sys_warning);
                builder.setMessage(R.string.clearQuotesDialogMessage);
                builder.setNegativeButton(android.R.string.no, null);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteQuotations();
                        adapter.clearQuotations();
                        item.setVisible(false);
                    }
                });
                builder.create().show();

                break;
            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemClickListener(int position) {
        String authorName = adapter.getQuotation(position).getAuthor();

        if (authorName == null || authorName.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.errorNoAuthor, Toast.LENGTH_SHORT);
            toast.show();
        }else {
            searchForAuthor(authorName);
        }

    }

    @Override
    public void onItemLongClickListener(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.stat_sys_warning);
        builder.setMessage(R.string.removeQuoteDialogMessage);
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteQuotation(adapter.getQuotation(position));
                adapter.removeQuotation(position);
            }
        });
        builder.create().show();
    }

    public void onQuotationsLoaded(List<Quotation> quotations)
    {
        // Si se ha recibido alguna cita, muestra la opción de borrar las citas favoritas.
        clearFavouritesItem.setVisible(quotations != null && quotations.size() > 0);

        adapter = new ModelViewAdapter(quotations, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = this.findViewById(R.id.quotesRecyclerView);
        RecyclerView.ItemDecoration separator = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(separator);
        recyclerView.setAdapter(adapter);
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
