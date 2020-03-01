package com.dadm.quotationshake.model;

public class Quotation {

    private String authorName;
    private String quote;

    public Quotation(String authorName, String quote){
        this.authorName = authorName;
        this.quote = quote;
    }

    public String getAuthorName(){
        return authorName;
    }

    public String getQuote(){
        return quote;
    }

    public void setAuthorName(String authorName){
        this.authorName = authorName;
    }

    public void setQuote(String quote){
        this.quote = quote;
    }
}
