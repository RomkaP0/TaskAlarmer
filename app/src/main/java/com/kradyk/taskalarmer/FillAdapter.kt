package com.kradyk.taskalarmer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
class FillAdapter(
    context: Context,
    private var viewResourceId: Int,
    private var items: ArrayList<FillItem>
) : ArrayAdapter<FillItem>(context, viewResourceId, items),Filterable {
    var itemsAll: ArrayList<FillItem> = items.clone() as ArrayList<FillItem>
    var suggestions: ArrayList<FillItem> = ArrayList()


    @SuppressLint("SetTextI18n")
    override fun getView(position:Int, convertView:View?, parent:ViewGroup):View {
        var v = convertView
        if (v == null) {
            val vi:LayoutInflater = context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(viewResourceId, null)
        }
        val fillItem: FillItem = items[position]
        val filllabel:TextView = v!!.findViewById(R.id.text_view_list_item)
        val filltime:TextView = v.findViewById(R.id.text_time_list_item)
        filllabel.text = fillItem.title
        filltime.text = fillItem.timeb+"-"+fillItem.timee
        return v
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return if (constraint!=null) {
                    suggestions.clear()
                    itemsAll.forEach {
                        if (it.title.lowercase().startsWith(constraint.toString().lowercase())){
                            suggestions.add(it)
                        }
                    }
                    val filterResults = FilterResults()
                    filterResults.values = suggestions
                    filterResults.count = suggestions.size
                    filterResults
                } else{
                    FilterResults()
                }
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?){
                if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<FillItem>

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

