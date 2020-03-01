package com.dadm.quotationshake.database;

import android.content.Context;
import android.provider.BaseColumns;

public class QuotationContract
{
    public enum Database { ROOM, SQLite }

    public static Database getPreferredDatabase(Context context)
    {
        return Database.SQLite;
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
