package com.kradyk.taskalarmer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.kradyk.taskalarmerimport.FillItem
import java.util.*
import kotlin.collections.ArrayList

class FillAdapter : ArrayAdapter<FillItem>,Filterable {
    var   items: ArrayList<FillItem>
    var itemsAll: ArrayList<FillItem>
    var suggestions: ArrayList<FillItem>
    var viewResourceId: Int = 0

    constructor(context:Context, viewResourceId:Int, items:ArrayList<FillItem>) : super(context, viewResourceId, items) {
        this.items = items
        this.itemsAll = items.clone() as ArrayList<FillItem>
        this.suggestions = ArrayList<FillItem>()
        this.viewResourceId = viewResourceId
    }


     override fun getView(position:Int, convertView:View?, parent:ViewGroup):View {
        var v = convertView
        if (v == null) {
            val vi:LayoutInflater = context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(viewResourceId, null)
        }
        var fillItem:FillItem = items.get(position)
        if (fillItem != null) {
            val filllabel:TextView = v!!.findViewById(R.id.text_view_list_item)
            val filltime:TextView = v.findViewById(R.id.text_time_list_item)
            if (filllabel != null) {
                filllabel.setText(fillItem.title)
                filltime.setText(fillItem.timeb+"-"+fillItem.timee)
            }
        }
        return v!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                if (constraint!=null) {
                    suggestions.clear()
                    itemsAll.forEach {
                        if (it.title.lowercase().startsWith(constraint.toString().lowercase())){
                            suggestions.add(it)
                        }
                    }
                    val filterResults:FilterResults = FilterResults()
                    filterResults.values = suggestions
                    filterResults.count = suggestions.size
                    return filterResults}
                else{
                    return FilterResults() }}
            override fun publishResults(constraint: CharSequence?, results: FilterResults?){
                val filteredList:ArrayList<FillItem> = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<FillItem>;

            }            }  };  }

//    @Override
//    public override fun getFilter():Filter {
//        return nameFilter
//    }

//     val nameFilter: Filter = Filter() {
//        public String convertResultToString(Object resultValue) {
//            String str = ((FillItem) (resultValue)).getTitle()
//            return str
//        }

//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            if (constraint != null) {
//                suggestions.clear()
//                for (FillItem fillItem : itemsAll) {
//                    if (fillItem.getTitle().toLowerCase()
//                            .startsWith(constraint.toString().toLowerCase())) {
//                        suggestions.add(fillItem)
//                    }
//                }
//                FilterResults filterResults = new FilterResults()
//                filterResults.values = suggestions
//                filterResults.count = suggestions.size()
//                return filterResults
//            } else {
//                return new FilterResults()
//            }
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint,
//            FilterResults results) {
//            @SuppressWarnings("unchecked")
//            ArrayList<FillItem> filteredList = (ArrayList<FillItem>) results.values
//            if (results != null && results.count > 0) {
//                clear()
//                for (FillItem c : filteredList) {
//                    add(c)
//                }
//                notifyDataSetChanged()
//            }
//        }
//    }

