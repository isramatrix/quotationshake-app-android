package com.dadm.quotationshake.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dadm.quotationshake.R;

import java.util.List;

public class ModelViewAdapter extends RecyclerView.Adapter<ModelViewAdapter.ViewHolder> {

    private List<Quotation> quotes;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    public ModelViewAdapter(List<Quotation> quotes, OnItemClickListener itemClickListener, OnItemLongClickListener itemLongClickListener){
        this.quotes = quotes;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    public Quotation getQuotation(int position){
        return quotes.get(position);
    }

    public void removeQuotation(int position){
        quotes.remove(position);
        notifyItemRemoved(position);
    }

    public void clearQuotations(){
        quotes.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View newView = inflater.inflate(R.layout.quotation_list_row, parent, false);

        return new ViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.quote.setText(quotes.get(position).getQuote());
        holder.authorName.setText(quotes.get(position).getAuthorName());
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClickListener(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView quote;
        public TextView authorName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quote = itemView.findViewById(R.id.quoteTextView);
            authorName = itemView.findViewById(R.id.authorNameTextView);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClickListener(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemLongClickListener.onItemLongClickListener(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
