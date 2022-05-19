package com.kradyk.taskalarmer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList


class SearchAdapter internal constructor(context: Context?, list: MutableList<SearchItem>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>(), Filterable {
    private val inflater: LayoutInflater
    private val list: List<SearchItem>
    private var listFull: List<SearchItem> = list

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = inflater.inflate(R.layout.cardlist, parent, false)
        return ViewHolder(view, object : SearchClickListener {
            override fun onCardClick(l: Int) {
                val it = list[l]
                val builderdlg =
                    MaterialAlertDialogBuilder(parent.context)
                        .setTitle(it.title)
                        .setMessage(
                            """${it.date}       ${it.timeb}-${it.timee}


${it.desc}"""
                        )
                        .setPositiveButton("Удалить") { _, _ -> }
                builderdlg.show()
            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.titleView.text = item.title
        holder.dateView.text = item.date
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View, listener: SearchClickListener) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private var listener: SearchClickListener
        val titleView: TextView
        val dateView: TextView
        private var cardView: MaterialCardView
        override fun onClick(view: View) {
            if (view.id == R.id.cardlst) listener.onCardClick(this.adapterPosition)
        }

        init {
            titleView = view.findViewById(R.id.title1)
            dateView = view.findViewById(R.id.date1)
            cardView = view.findViewById(R.id.cardlst)
            this.listener = listener
            cardView.setOnClickListener(this)
        }
    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    private val searchFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<SearchItem> = ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(listFull)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in listFull) {
                    if (item.title.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence, results: FilterResults) {
            list.clear()
            list.addAll(results.values as List<SearchItem>)
            notifyDataSetChanged()
        }
    }

    interface SearchClickListener {
        fun onCardClick(l: Int)
    }

    init {
        this.list = list
        inflater = LayoutInflater.from(context)
        listFull = ArrayList(list)
    }
}