package com.kradyk.taskalarmer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

public class FillAdapter extends ArrayAdapter<FillItem> {
    private ArrayList<FillItem> items;
    private ArrayList<FillItem> itemsAll;
    private ArrayList<FillItem> suggestions;
    private int viewResourceId;

    @SuppressWarnings("unchecked")
    public FillAdapter(Context context, int viewResourceId, ArrayList<FillItem> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<FillItem>) items.clone();
        this.suggestions = new ArrayList<FillItem>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        FillItem fillItem = items.get(position);
        if (fillItem != null) {
            TextView filllabel = (TextView)  v.findViewById(R.id.text_view_list_item);
            TextView filltime = v.findViewById(R.id.text_time_list_item);
            if (filllabel != null) {
                filllabel.setText(fillItem.getTitle());
                filltime.setText(fillItem.getTimeb()+"-"+fillItem.getTimee());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            String str = ((FillItem) (resultValue)).getTitle();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (FillItem fillItem : itemsAll) {
                    if (fillItem.getTitle().toLowerCase()
                            .startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(fillItem);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            @SuppressWarnings("unchecked")
            ArrayList<FillItem> filteredList = (ArrayList<FillItem>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (FillItem c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}