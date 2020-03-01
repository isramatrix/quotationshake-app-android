package com.dadm.quotationshake.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dadm.quotationshake.R;
import com.dadm.quotationshake.model.database.QuotationContract;
import com.dadm.quotationshake.model.database.QuotationRoom;
import com.dadm.quotationshake.model.database.QuotationSQLiteOpenHelper;
import com.dadm.quotationshake.model.entity.Quotation;

public class QuotationActivity extends AppCompatActivity {

    private static final String DEFAULT_USERNAME = "Nameless One";

    private static final String BUNDLE_QUOTE = "quote";

    private static final String BUNDLE_AUTHOR = "author";

    private static int receivedQuotations = 0;

    private static QuotationContract.Database preferedDatabase;

    private TextView quoteView;

    private TextView authorView;

    private MenuItem refreshItem;

    private MenuItem addFavouritesItem;

    private Quotation actualQuotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        quoteView = findViewById(R.id.quote_view);
        setWelcomeText(quoteView);

        authorView = findViewById(R.id.author_view);

        // Si existía contenido en la actividad anterior, la mantiene
        if (savedInstanceState != null) {
            quoteView.setText(savedInstanceState.getString(BUNDLE_QUOTE));
            authorView.setText(savedInstanceState.getString(BUNDLE_AUTHOR));
        }

        preferedDatabase = QuotationContract.getPreferredDatabase(this);
    }

    private void setWelcomeText(TextView quoteView)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtiene el nombre de las preferencias del usuario. Si no existe se
        // añade un nombre por defecto
        String userName = preferences.getString(
                getString(R.string.username), DEFAULT_USERNAME);

        // Incluye el nombre del usuario en el texto de la vista de citas.
        String quoteText = String.format(
                getText(R.string.refresh_to_quotation).toString(), userName);

        // Añade el texto a la vista.
        quoteView.setText(quoteText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Guarda una referencia de los items del menú
        refreshItem = menu.getItem(0);
        addFavouritesItem = menu.getItem(1);

        // Inicia la invisibilidad del botón de añadir favoritos
        addFavouritesItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // Si la opción es para obtener una nueva cita.
        if (item.equals(refreshItem)) getNewQuote();

        // Si la opción es para añadir la cita a favoritos.
        else if (item.equals(addFavouritesItem)) addQuoteToFavourites(actualQuotation);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(BUNDLE_QUOTE, quoteView.getText().toString());
        outState.putString(BUNDLE_AUTHOR, authorView.getText().toString());
    }

    private void getNewQuote()
    {
        // Recupera una cita desde base de datos.
        actualQuotation = fetchQuotation();

        // Añade el texto de la cita recuperada y la muestra al usuario.
        quoteView.setText(actualQuotation.getQuote());
        authorView.setText(actualQuotation.getAuthor());

        // Si no existe la cita en base de datos, permite añadira haciendo aparecer el botón ADD.
        addFavouritesItem.setVisible(!existsQuote(actualQuotation));

        // Suma la cuenta de citas obtenidas.
        addQuoteCount();
    }

    private Quotation fetchQuotation()
    {
        return new Quotation("a" + receivedQuotations, "b");
    }

    private void addQuoteCount()
    {
        receivedQuotations++;
    }

    private void addQuoteToFavourites(Quotation quotation)
    {
        switch (preferedDatabase)
        {
            case SQLite:
                QuotationSQLiteOpenHelper.getInstance(this).addQuotation(quotation);
                break;

            case ROOM:
                QuotationRoom.getInstance(this).quotationDao().add(quotation);
                break;
        }

        addFavouritesItem.setVisible(false);
    }

    private boolean existsQuote(Quotation quotation)
    {
        switch (preferedDatabase)
        {
            case SQLite:
                return QuotationSQLiteOpenHelper.getInstance(this).existsQuotation(quotation);

            case ROOM:
                return QuotationRoom.getInstance(this).quotationDao().get(quotation.getQuote()) != null;

            default: return false;
        }
    }
}
