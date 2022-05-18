package com.kradyk.taskalarmer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kradyk.taskalarmerimport.DBHelper
import com.kradyk.taskalarmerimport.Item
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog

class ItemAdapter internal constructor(
    context: Context?,
    private val items: List<Item>,
    notif: Array<String>,
    repeate: Array<String>
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    var dbHelper: DBHelper? = null
    private val inflater: LayoutInflater
    private var pendingIntent: PendingIntent? = null
    private var alarmManager: AlarmManager? = null
    private val notif: Array<String>
    private val repeate: Array<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.cardview, parent, false)
        return ViewHolder(view, object : MyClickListener {
            override fun onEdit(p: Int) {
                Log.d("adLog", Integer.toString(p))
                val it = items[p]
                val i = Intent("com.example.CreateEventActivity")
                val extras = Bundle()
                extras.putString("date", it.date)
                extras.putString("from", "rv")
                extras.putString("id", it.id)
                extras.putString("title", it.title)
                extras.putString("timeb", it.timeb)
                extras.putString("timee", it.timee)
                extras.putString("desc", it.desc)
                extras.putString("notify", it.timenotif)
                extras.putString("repeat", it.repeat)
                extras.putString("paral", it.paral)
                i.putExtras(extras)
                ContextCompat.startActivity(parent.context, i, null)
            }

            @SuppressLint("UnspecifiedImmutableFlag")
            override fun onDelete(p: Int) {
                Log.d("adLog", Integer.toString(p))
                val it = items[p]
                dbHelper = DBHelper(parent.context, "String", 1)
                val db = dbHelper!!.writableDatabase
                db.delete("events", "_id = ?", arrayOf(it.id))
                val intent = Intent(parent.context, AlarmReceiver::class.java)
                pendingIntent = PendingIntent.getBroadcast(parent.context, it.id.toInt(), intent, 0)
                if (alarmManager == null) {
                    alarmManager =
                        parent.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                }
                alarmManager!!.cancel(pendingIntent)
            }

            override fun onShare(p: Int) {
                val it = items[p]
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    """${it.title} ${parent.resources.getString(R.string.schedule)}
${it.date}    ${it.timeb}-${it.timee}
${it.desc}"""
                )
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, null)
                ContextCompat.startActivity(parent.context, shareIntent, Bundle.EMPTY)
            }

            override fun onCardClick(p: Int) {

            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleView.text = item.title
        val str = item.timeb + "-" + item.timee
//        if (item.cat == 2) holder.imgline.setColorFilter(
//            ContextCompat.getColor(
//                holder.imageView.context,
//                R.color.teal_200
//            )
//        )
//        if (item.cat == 1) holder.imgline.setColorFilter(
//            ContextCompat.getColor(
//                holder.imageView.context,
//                R.color.purple_500
//            )
//        )
//        if (item.cat == 0) holder.imgline.setColorFilter(
//            ContextCompat.getColor(
//                holder.imageView.context,
//                R.color.purple_700
//            )
//        )
        holder.notifyat.setText(R.string.timenotif)
        if (item.timenotif == "At moment" || item.timenotif == "В момент") holder.notifyat.append(" " + notif[0]) else if (item.timenotif.contains(
                "5"
            )
        ) holder.notifyat.append(" " + notif[1]) else if (item.timenotif.contains("1")) {
            if (item.timenotif.contains("10")) holder.notifyat.append(" " + notif[2]) else if (item.timenotif.contains(
                    "12"
                )
            ) holder.notifyat.append(" " + notif[6]) else holder.notifyat.append(" " + notif[4])
        } else if (item.timenotif.contains("3")) holder.notifyat.append(" " + notif[3]) else if (item.timenotif.contains(
                "6"
            )
        ) holder.notifyat.append(" " + notif[5]) else if (item.timenotif.contains("2")) holder.notifyat.append(
            " " + notif[7]
        ) else holder.notifyat.append(" " + notif[8])
        if (item.repeat == "Нет" || item.repeat == "No") {
            holder.imageView.visibility = View.GONE
        } else {
            if (item.repeat == "Daily" || item.repeat == "Ежедневно")
                holder.dateView.text = repeate[1]
            else if (item.repeat == "Weekly" || item.repeat == "Еженедельно")
                holder.dateView.text= repeate[2]
            else if (item.repeat == "Monthly" || item.repeat == "Ежемесячно")
                holder.dateView.text = repeate[3]
             else holder.dateView.text = repeate[4]
        }
        holder.timeView.text = str
        if (item.paral == "1") holder.parall.setText(R.string.paral) else holder.parall.visibility =
            View.GONE
        holder.descView.text = item.desc
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View, listener: MyClickListener) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var listener: MyClickListener
        val titleView: TextView
        val timeView: TextView
        val dateView: TextView
        val descView: TextView
        val notifyat: TextView
        val parall: TextView
        val edit: ImageButton
        val delete: ImageButton
        val share: ImageButton
        var imageView: ImageView
        var constraintLayout: ConstraintLayout
        val layoutbtn:LinearLayout
        val layoutunnecer: LinearLayout
        @SuppressLint("NonConstantResourceId")
        override fun onClick(view: View) {
            when (view.id) {
                R.id.edit -> listener.onEdit(this.adapterPosition)
                R.id.layoutdis ->{ listener.onCardClick(this.adapterPosition)
                    if (layoutbtn.visibility==View.VISIBLE){
                layoutbtn.visibility = View.GONE
                layoutunnecer.visibility = View.GONE}
                else{
                        layoutbtn.visibility = View.VISIBLE
                        layoutunnecer.visibility = View.VISIBLE
                }}
                R.id.delete -> {
                    val builderdlg = BottomSheetMaterialDialog.Builder(
                        (view.context as Activity)
                    )
                        .setTitle(view.context.resources.getString(R.string.deleteev))
                        .setAnimation(R.raw.delete_anim)
                        .setMessage(
                            """
    
    ${view.resources.getString(R.string.deleteevbody)}
    
    """.trimIndent()
                        )
                        .setPositiveButton("OK") { dialogInterface, which ->
                            listener.onDelete(adapterPosition)
                            constraintLayout.visibility = View.INVISIBLE
                            constraintLayout.isClickable = false
                            constraintLayout.maxHeight = 1
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton(view.context.resources.getString(R.string.cancel)) { dialogInterface, which -> dialogInterface.dismiss() }
                        .build()
                    builderdlg.show()
                }
                R.id.share -> listener.onShare(adapterPosition)
                else -> {}
            }
        }

        init {
            titleView = view.findViewById(R.id.title1)
            timeView = view.findViewById(R.id.time1)
            dateView = view.findViewById(R.id.date1)
            descView = view.findViewById(R.id.desc)
            edit = view.findViewById(R.id.edit)
            imageView = view.findViewById(R.id.repeatingIC)
            notifyat = view.findViewById(R.id.notify)
            delete = view.findViewById(R.id.delete)
            parall = view.findViewById(R.id.parall)
            share = view.findViewById(R.id.share)
            constraintLayout = view.findViewById(R.id.layoutdis)
            layoutbtn = view.findViewById(R.id.layoutbtn)
            layoutunnecer = view.findViewById(R.id.layoutunnecer)

            this.listener = listener
            edit.setOnClickListener(this)
            delete.setOnClickListener(this)
            share.setOnClickListener(this)
            constraintLayout.setOnClickListener(this)
        }
    }

    interface MyClickListener {
        fun onEdit(p: Int)
        fun onDelete(p: Int)
        fun onShare(p: Int)
        fun onCardClick(p:Int)
    }

    init {
        inflater = LayoutInflater.from(context)
        this.notif = notif
        this.repeate = repeate
    }
}