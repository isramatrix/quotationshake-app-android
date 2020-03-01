package com.dadm.quotationshake.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dadm.quotationshake.model.entity.Quotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dadm.quotationshake.model.database.QuotationContract.Columns.*;
import static com.dadm.quotationshake.model.database.QuotationContract.DATABASE_NAME;

public class QuotationSQLiteOpenHelper extends SQLiteOpenHelper
{
    private static QuotationSQLiteOpenHelper Instance;

    public synchronized static QuotationSQLiteOpenHelper getInstance(Context context)
    {
        return Instance == null ? Instance = new QuotationSQLiteOpenHelper(context) : Instance;
    }

    private QuotationSQLiteOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    private QuotationSQLiteOpenHelper(@Nullable Context context, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, 1, errorHandler);
    }

    @RequiresApi(28)
    private QuotationSQLiteOpenHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        initializeDatabase(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }

    private void initializeDatabase(SQLiteDatabase database)
    {
        Map<String, String> quotationColumns = new HashMap<>();
        quotationColumns.put(QUOTATION_ID, "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL");
        quotationColumns.put(QUOTATION_QUOTE, "TEXT NOT NULL");
        quotationColumns.put(QUOTATION_AUTHOR, "TEXT NOT NULL");

        database.execSQL(getCreateTableQuery(QUOTATION_TABLE, quotationColumns));
    }

    private String getCreateTableQuery(String tableName, Map<String, String> columns)
    {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE").append(" ");
        query.append(tableName).append("\n");
        query.append("( ");

        for (String column : columns.keySet()) {
            query.append(column).append(" ");
            query.append(columns.get(column)).append(" ");
            query.append(",").append("\n");
        }

        query.setCharAt(query.length() - 2, ' ');

        query.append(")").append(";");

        return query.toString();
    }

    public List<Quotation> getQuotations()
    {
        SQLiteDatabase database = getReadableDatabase();
        List<Quotation> result = new ArrayList<>();

        // Ejecuta una SELECT sobre la base de datos y obtiene el resultado
        Cursor query = database.query(
                QUOTATION_TABLE,
                new String[]{ QUOTATION_QUOTE, QUOTATION_AUTHOR },
                null, null, null, null, null
        );

        // Itera sobre las filas devueltas para crear las entidades de citas.
        while (query.moveToNext())
            result.add( new Quotation(
                    query.getString(query.getColumnIndex(QUOTATION_QUOTE)),
                    query.getString(query.getColumnIndex(QUOTATION_AUTHOR))
            ));
        query.close();

        // Devuelve la lista de citas guardadas.
        return result;
    }

    public boolean existsQuotation(Quotation quotation)
    {
        SQLiteDatabase database = getReadableDatabase();
        boolean found;

        // Ejecuta una SELECT sobre la base de datos y obtiene sólo las que sean igaules al texto especificado.
        Cursor query = database.query(
                QUOTATION_TABLE,
                null,
                QUOTATION_QUOTE + " = ?",
                new String[] { quotation.getQuote() },
                null, null, null
        );

        // Devuelve si se ha obtenido algún resultado (Si se ha encontrado la cita especificada).
        found = query.getCount() > 0;
        query.close();

        return found;
    }

    public void addQuotation(Quotation quotation)
    {
        SQLiteDatabase database = getWritableDatabase();

        // Crea los pares de valores a añadir a la base de datos.
        ContentValues values = new ContentValues();
        values.put(QUOTATION_QUOTE, quotation.getQuote());
        values.put(QUOTATION_AUTHOR, quotation.getAuthor());

        // Ejecuta un INSERT sobre la base de datos con la cita especificada.
        database.insert(QUOTATION_TABLE, null, values);
        database.close();
    }

    public void deleteQuotation(Quotation quotation)
    {
        SQLiteDatabase database = getWritableDatabase();

        // Ejecuta un DELETE para eliminar la cita especificada de base de datos.
        database.delete(
                QUOTATION_TABLE,
                QUOTATION_QUOTE + " = ?",
                new String[] { quotation.getQuote() });
        database.close();
    }

    public void deleteAllQuotations()
    {
        SQLiteDatabase database = getWritableDatabase();

        // Ejecuta un DELETE para eliminar todas las citas de base de datos.
        database.delete(QUOTATION_TABLE, null, null);
        database.close();
    }
}
