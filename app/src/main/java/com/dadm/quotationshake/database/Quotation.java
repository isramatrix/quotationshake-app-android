package com.dadm.quotationshake.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import static com.dadm.quotationshake.database.QuotationContract.Columns.*;

@Entity(tableName = QUOTATION_TABLE)
public class Quotation
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = QUOTATION_ID)
    private int id;

    @NonNull
    @SerializedName("quoteText")
    @ColumnInfo(name = QUOTATION_QUOTE)
    private String quote;

    @NonNull
    @SerializedName("quoteAuthor")
    @ColumnInfo(name = QUOTATION_AUTHOR)
    private String author;

    public Quotation() { }

    @Ignore
    public Quotation(@NonNull String quote, @NonNull String author)
    {
        this.quote = quote;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getQuote() {
        return quote;
    }

    public void setQuote(@NonNull String quote) {
        this.quote = quote;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull String author) {
        this.author = author;
    }
}
