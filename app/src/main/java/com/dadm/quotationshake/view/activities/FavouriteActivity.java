package com.dadm.quotationshake.view.activities;

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
import android.view.View;
import android.widget.Toast;

import com.dadm.quotationshake.R;
import com.dadm.quotationshake.model.ModelViewAdapter;
import com.dadm.quotationshake.model.Quotation;

import java.util.ArrayList;

public class FavouriteActivity extends AppCompatActivity implements ModelViewAdapter.OnItemClickListener, ModelViewAdapter.OnItemLongClickListener {

    private static final String WIKI_URL = "https://en.wikipedia.org/wiki/Special:Search";

    private String authorName = "Albert Einstein";
    private ModelViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        ArrayList quotes = new ArrayList<Quotation>();
        quotes.add(new Quotation("Calderón de la barca", "Arde, pero no arde"));

        adapter = new ModelViewAdapter(quotes, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = this.findViewById(R.id.quotesRecyclerView);
        RecyclerView.ItemDecoration separator = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(separator);
        recyclerView.setAdapter(adapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (adapter.getItemCount() == 0){
            menu.findItem(R.id.clearAllFavsMenuItem).setVisible(false);
        }
        getMenuInflater().inflate(R.menu.favourite_activity_menu,menu);
        return true;
    }

    @Override
    public void onItemClickListener(int position) {
        String authorName = adapter.getQuotation(position).getAuthorName();

        if (authorName == null || authorName.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.errorNoAuthor, Toast.LENGTH_SHORT);
            toast.show();
        }else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getSearchUrl(authorName)));
            startActivity(intent);
        }

    }

    @Override
    public void onItemLongClickListener(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.stat_sys_warning);
        builder.setMessage(R.string.removeQuoteDialogMessage);
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.removeQuotation(position);
            }
        });
        builder.create().show();
    }
}
