package com.kradyk.taskalarmer;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter  extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

    private final LayoutInflater inflater;
    private List<SearchItem> list;
    private List<SearchItem> listFull;
    DBHelper dbHelper;

    SearchAdapter(Context context, List<SearchItem> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        listFull = new ArrayList<>(list);

    }
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.cardlist, parent, false);
        ViewHolder holder = new ViewHolder(view, new SearchClickListener() {
            @Override
            public void onCardClick(int l) {
                SearchItem it = list.get(l);
                MaterialAlertDialogBuilder builderdlg= new MaterialAlertDialogBuilder(parent.getContext())
                        .setTitle(it.getTitle())
                        .setMessage(it.getDate()+"       "+it.getTimeb()+"-"+it.getTimee()+"\n"+"\n\n"+it.getDesc())
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builderdlg.show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        SearchItem item = list.get(position);
        holder.titleView.setText(item.getTitle());
        holder.dateView.setText(item.getDate());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SearchClickListener listener;
        final TextView titleView, dateView;
        MaterialCardView cardView;
        public ViewHolder(View view, SearchClickListener listener){
            super(view);
            titleView = view.findViewById(R.id.title1);
            dateView=view.findViewById(R.id.date1);
            cardView = view.findViewById(R.id.cardlst);
            this.listener = listener;

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.cardlst)
                listener.onCardClick(this.getAdapterPosition());
        }
    }
    public Filter getFilter(){
        return searchFilter;
    }
    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SearchItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length()==0){
                filteredList.addAll(listFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SearchItem item: listFull){
                    if(item.getTitle().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            list.clear();
            list.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };
public interface SearchClickListener{
    void onCardClick(int l);

}

    }