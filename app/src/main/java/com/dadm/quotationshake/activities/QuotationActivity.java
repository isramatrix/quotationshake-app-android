package com.dadm.quotationshake.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dadm.quotationshake.R;
import com.dadm.quotationshake.tasks.CallQuotationTask;
import com.dadm.quotationshake.database.QuotationContract;
import com.dadm.quotationshake.database.QuotationRoom;
import com.dadm.quotationshake.database.QuotationSQLiteOpenHelper;
import com.dadm.quotationshake.database.Quotation;

public class QuotationActivity extends AppCompatActivity {

    private static final String DEFAULT_USERNAME = "Nameless One";

    private static final String BUNDLE_QUOTE = "quote";

    private static final String BUNDLE_AUTHOR = "author";

    private static QuotationContract.Database preferredDatabase;

    private TextView quoteView;

    private TextView authorView;

    private ImageButton refreshButton;

    private ProgressBar quotationLoading;

    private MenuItem refreshItem;

    private MenuItem addFavouritesItem;

    private Quotation actualQuotation;

    private AsyncTask callQuotationTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        preferredDatabase = QuotationContract.getPreferredDatabase(this);

        // Inicializa la referencia al botón que actualiza las citas.
        refreshButton = findViewById(R.id.refreshMenuItem);

        // Inicializa la referencia a la animación de cargarndo una cita.
        quotationLoading = findViewById(R.id.quotation_loading);

        // Inicializa la referencia al texto que muestra la cita.
        quoteView = findViewById(R.id.quote_view);
        setWelcomeText(quoteView);

        // Inicializa la referencia al texto que muestra el autor de la cita.
        authorView = findViewById(R.id.author_view);

        // Si existía contenido en la actividad anterior, la mantiene.
        if (savedInstanceState != null) {
            quoteView.setText(savedInstanceState.getString(BUNDLE_QUOTE));
            authorView.setText(savedInstanceState.getString(BUNDLE_AUTHOR));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.quotation_activity_menu, menu);

        addFavouritesItem = menu.getItem(0);
        refreshItem = menu.getItem(1);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.favoriteMenuItem: addQuoteToFavourites(actualQuotation); break;
            case R.id.refreshMenuItem: if (isInternetAvailable()) callQuotationTask = createQuotationTask(); break;
            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private AsyncTask createQuotationTask()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtiene los parámetros de la petición de las preferencias del usuario.
        String language = preferences.getString(getString(R.string.quotations_title), "");
        String requestMethod = preferences.getString(getString(R.string.request_title), "");

        return new CallQuotationTask(this).execute(language, requestMethod);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(BUNDLE_QUOTE, quoteView.getText().toString());
        outState.putString(BUNDLE_AUTHOR, authorView.getText().toString());
    }

    @Override
    protected void onStop()
    {
        if (callQuotationTask != null && callQuotationTask.getStatus() == AsyncTask.Status.RUNNING)
            callQuotationTask.cancel(true);
        super.onStop();
    }

    /**
     * Añade el texto pricipal a la vista donde se mostrarán las citas cargadas.
     *
     * @param quoteView La vista que contiene el texto.
     */
    private void setWelcomeText(TextView quoteView)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtiene el nombre de las preferencias del usuario. Si no existe se
        // añade un nombre por defecto
        String userName = preferences.getString(getString(R.string.username), DEFAULT_USERNAME);
        if (userName.isEmpty()) userName = DEFAULT_USERNAME;

        // Incluye el nombre del usuario en el texto de la vista de citas.
        String quoteText = String.format(
                getText(R.string.refresh_to_quotation).toString(), userName);

        // Añade el texto a la vista.
        quoteView.setText(quoteText);
    }

    /**
     * Muestra en la interfaz el texto y el autor de una cita especificada
     * y comprueba si dicha cita se puede añadir como favorita en base de datos.
     *
     * @param quotation La cita a mostrar
     */
    public void showQuotation(Quotation quotation)
    {
        // Guarda una referencia a la nueva cita obtenida.
        actualQuotation = quotation;

        // Añade el texto de la cita recuperada y la muestra al usuario.
        quoteView.setText(actualQuotation.getQuote());
        authorView.setText(actualQuotation.getAuthor());

        // Si no existe la cita en base de datos, permite añadira haciendo aparecer el botón ADD.
        existsQuote(actualQuotation, addFavouritesItem);
    }

    /**
     * Modifica la iterfaz para mostrar que una cita se está cargando y recuperando
     * de la API o ya está disponible para mostrarse en la interfaz.
     *
     * @param loading Si se está cargando una cita.
     */
    public void setLoadingQuotation(boolean loading)
    {
        quoteView.setVisibility(loading ? View.GONE : View.VISIBLE);
        authorView.setVisibility(loading ? View.GONE : View.VISIBLE);
        refreshItem.setVisible(!loading);
        addFavouritesItem.setVisible(!loading);
        quotationLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    /**
     * Comprueba si el dispositivo tiene conexión a Internet en el momento de
     * la consulta a este método.
     *
     * @return Si el dispositivo tiene conexión a Internet.
     */
    private boolean isInternetAvailable()
    {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Si no hay tarjeta de red en el dispositivo, no se puede conectar a Internet.
        if (connectivity == null) return false;

        // De igual manera, si la tarjeta de red no está disponible, no se puede conectar a Internet.
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Añade una cita especifiacada a base de datos local.
     *
     * @param quotation La cita a añadir.
     */
    private void addQuoteToFavourites(final Quotation quotation)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (preferredDatabase)
                {
                    case SQLite:
                        QuotationSQLiteOpenHelper.getInstance(QuotationActivity.this).addQuotation(quotation);
                        break;

                    case ROOM:
                        QuotationRoom.getInstance(QuotationActivity.this).quotationDao().add(quotation);
                        break;
                }
            }
        }).start();

        addFavouritesItem.setVisible(false);
    }

    /**
     * Comprueba si una cita concreata existe en base de datos y habilita o no la opción
     * de añadirla en base de datos en caso de que no exitsa ya.
     *
     * @param quotation La cita a comprobar
     * @param addFavouritesItem El item del menú que permita guardar en base de datos.
     */
    private void existsQuote(final Quotation quotation, final MenuItem addFavouritesItem)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                switch (preferredDatabase)
                {
                    case SQLite:
                        final boolean existsSql = QuotationSQLiteOpenHelper.getInstance(QuotationActivity.this).existsQuotation(quotation);
                        QuotationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addFavouritesItem.setVisible(!existsSql);
                            }
                        });
                    break;

                    case ROOM:
                        final boolean existsRoom = QuotationRoom.getInstance(QuotationActivity.this).quotationDao().get(quotation.getQuote()) != null;
                        QuotationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addFavouritesItem.setVisible(!existsRoom);
                            }
                        });
                        break;

                    default: addFavouritesItem.setVisible(true);
                }
            }
        }).start();
    }
}
