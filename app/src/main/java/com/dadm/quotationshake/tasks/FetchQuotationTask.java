package com.dadm.quotationshake.tasks;

import android.content.Context;
import android.os.AsyncTask;

import static com.dadm.quotationshake.database.QuotationContract.Database;

import com.dadm.quotationshake.database.QuotationRoom;
import com.dadm.quotationshake.database.QuotationSQLiteOpenHelper;
import com.dadm.quotationshake.database.Quotation;
import com.dadm.quotationshake.activities.FavouriteActivity;

import java.lang.ref.WeakReference;
import java.util.List;

public class FetchQuotationTask extends AsyncTask<Database, Void, List<Quotation>> {

    private final WeakReference<FavouriteActivity> activity;

    public FetchQuotationTask(FavouriteActivity activity)
    {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    protected List<Quotation> doInBackground(Database... preferredDatabase)
    {
        // Obtiene una referencia al contexto de ejecución del hilo
        Context context = this.activity.get();
        if (context == null) return null;

        switch (preferredDatabase[0])
        {
            case SQLite:
                return QuotationSQLiteOpenHelper.getInstance(context).getQuotations();

            case ROOM:
                return QuotationRoom.getInstance(context).quotationDao().getAll();

            default: return null;
        }
    }

    @Override
    protected void onPostExecute(List<Quotation> quotations)
    {
        super.onPostExecute(quotations);

        // Obtiene una referencia al contexto de ejecución del hilo
        FavouriteActivity context = this.activity.get();
        if (context == null) return;

        // Devuelve la lista de citas obtenida de base de datos a la actividad.
        context.onQuotationsLoaded(quotations);
    }
}
