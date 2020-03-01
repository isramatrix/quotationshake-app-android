package com.dadm.quotationshake.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuotationDao
{
    @Insert
    void add(Quotation quotation);

    @Delete
    void delete(Quotation quotation);

    @Query("SELECT * FROM quotation_table")
    List<Quotation> getAll();

    @Query("SELECT * FROM quotation_table WHERE quote = :quoteText")
    Quotation get(String quoteText);

    @Query("DELETE FROM quotation_table")
    void deleteAll();
}
