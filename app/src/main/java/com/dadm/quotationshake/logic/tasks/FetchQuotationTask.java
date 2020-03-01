package com.dadm.quotationshake.logic.tasks;

import android.content.Context;
import android.os.AsyncTask;

import static com.dadm.quotationshake.model.database.QuotationContract.Database;

import com.dadm.quotationshake.model.database.QuotationRoom;
import com.dadm.quotationshake.model.database.QuotationSQLiteOpenHelper;
import com.dadm.quotationshake.model.entity.Quotation;
import com.dadm.quotationshake.view.activities.FavouriteActivity;

import java.lang.ref.WeakReference;
import java.util.List;

public class FetchQuotationTask extends AsyncTask<Database, Void, List<Quotation>> {

    private final WeakReference<FavouriteActivity> activity;

    public FetchQuotationTask(FavouriteActivity activity)
    {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    protected List<Quotation> doInBackground(Database... preferedDatabase)
    {
        // Obtiene una referencia al contexto de ejecución del hilo
        Context context = this.activity.get();
        if (context == null) return null;

        switch (preferedDatabase[0])
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
