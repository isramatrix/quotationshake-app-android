package com.dadm.quotationshake.logic.tasks;

import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;

import com.dadm.quotationshake.R;
import com.dadm.quotationshake.model.entity.Quotation;
import com.dadm.quotationshake.view.activities.QuotationActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CallQuotationTask extends AsyncTask<String, Void, Quotation>
{
    public enum Method { GET, POST, ANY }

    public enum Language { EN, RU, ANY }

    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.forismatic.com";
    private static final String PATH = "/api/1.0/";

    private final WeakReference<QuotationActivity> activity;

    public CallQuotationTask(QuotationActivity activity)
    {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        // Obtiene una referencia al contexto de ejecución del hilo
        QuotationActivity context = this.activity.get();
        if (context == null) return;

        // Notifica a la actividad que se está cargando una nueva cita.
        context.setLoadingQuotation(true);
    }

    @Override
    protected Quotation doInBackground(String... params)
    {
        try {

            // Obtiene los parámetros de la tarea asíncrona.
            Language language = getLanguage(params[0]);
            Method requestMethod = getMethod(params[1]);

            // Abre y configura una nueva conexión a la API de citas.
            HttpsURLConnection connection = openQuotationConnection(requestMethod, language);
            configureConnection(requestMethod, connection);

            // Si el método elegido por el usuario es POST, añade el cuerpo de la petición.
            if (requestMethod == Method.POST)
                setRequestBody(language, connection);

            // Si no hay ningún problema con la conexión, devuelve la cita obtenida.
            if (isConnectionAvailable(connection))
                return readConnectionResponse(connection);

            // En caso contrario, devuelve una cita nula.
            else return null;

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }

        // Si ha habido algún error en la petición, devuelve un valor nulo.
        return null;
    }

    private HttpsURLConnection openQuotationConnection(Method requestMethod, Language language) throws MalformedURLException, IOException
    {
        // Construye la URL base de la petición.
        Uri.Builder uri = new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH);

        // Si el tipo de petición HTTP es GET, añade el idioma en la URL.
        if (requestMethod == Method.GET) {
            uri.appendQueryParameter("method", "getQuote");
            uri.appendQueryParameter("format", "json");
            uri.appendQueryParameter("lang", language.name().toLowerCase());
        }

        // Devuelve una conexión abierta a la URL de obtención de citas.
        return (HttpsURLConnection) new URL(uri.build().toString()).openConnection();
    }

    private void configureConnection(Method requestMethod, HttpURLConnection connection) throws ProtocolException
    {
        connection.setRequestMethod(requestMethod.name());
    }

    private void setRequestBody(Language language, HttpURLConnection connection) throws IOException
    {
        OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
        os.write("mehod=getQuote&format=json&lang=" + language.name().toLowerCase());
        os.flush(); os.close();
    }

    private Quotation readConnectionResponse(HttpsURLConnection connection) throws IOException
    {
        InputStreamReader is = new InputStreamReader(connection.getInputStream());
        Quotation quotation = new Gson().fromJson(is, Quotation.class);
        is.close();
        return quotation;
    }

    private boolean isConnectionAvailable(HttpURLConnection connection) throws IOException
    {
        return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    private Method getMethod(String requestMethod)
    {
        if (requestMethod.equals(Resources.getSystem().getString(R.string.get)))
            return Method.GET;
        else if (requestMethod.equals(Resources.getSystem().getString(R.string.post)))
            return Method.POST;

        else return Method.ANY;
    }

    private Language getLanguage(String language)
    {
        if (language.equals(Resources.getSystem().getString(R.string.get)))
            return Language.EN;
        else if (language.equals(Resources.getSystem().getString(R.string.post)))
            return Language.RU;

        else return Language.ANY;
    }

    @Override
    protected void onPostExecute(Quotation quotation)
    {
        super.onPostExecute(quotation);

        // Obtiene una referencia al contexto de ejecución del hilo
        QuotationActivity context = this.activity.get();
        if (context == null) return;

        // Muestra el contenido de la cita recuperada en la actividad.
        context.showQuotation(quotation);

        // Notifica a la interfaz que se ha terminado de cargar la nueva cita.
        context.setLoadingQuotation(true);
    }
}
