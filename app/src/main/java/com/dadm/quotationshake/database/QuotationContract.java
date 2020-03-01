package com.dadm.quotationshake.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.BaseColumns;

import androidx.preference.PreferenceManager;

import com.dadm.quotationshake.R;

public class QuotationContract
{
    public enum Database { ROOM, SQLite }

    public static Database getPreferredDatabase(Context context)
    {
        // Obtiene la base de datos de la preferencias del usuario.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedDatabase = preferences.getString(
                context.getString(R.string.database_preference),
                context.getString(R.string.sqlite_open_helper));

        if (selectedDatabase.equals(context.getString(R.string.room)))
            return Database.ROOM;
        else if (selectedDatabase.equals(context.getString(R.string.sqlite_open_helper)))
            return Database.SQLite;
        else
            return null;
    }

    public static final String DATABASE_NAME = "quotation_database";

    private QuotationContract() { }

    public static class Columns implements BaseColumns
    {
        public static final String QUOTATION_TABLE = "quotation_table";
        public static final String QUOTATION_ID = "id";
        public static final String QUOTATION_QUOTE = "quote";
        public static final String QUOTATION_AUTHOR = "author";

    }
}
