package com.dadm.quotationshake.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import static com.dadm.quotationshake.database.QuotationContract.DATABASE_NAME;

@Database(
        entities = { Quotation.class },
        version = 1,
        exportSchema = false
)
public abstract class QuotationRoom extends RoomDatabase
{
    private static QuotationRoom Instance;

    public synchronized static QuotationRoom getInstance(Context context)
    {
        return Instance == null ? Instance = createDatabase(context) : Instance;
    }

    private static QuotationRoom createDatabase(Context context)
    {
        return Room.databaseBuilder(context, QuotationRoom.class, DATABASE_NAME)
                .build();
    }

    public QuotationRoom() { }

    public abstract QuotationDao quotationDao();
}
