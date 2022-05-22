package com.kradyk.taskalarmer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.getbase.floatingactionbutton.FloatingActionButton
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import java.util.*


class MainFragment : Fragment() {
    private var day = 0
    private var month = 0
    private var yr = 0
    private lateinit var mcalendarView: CalendarView
    private lateinit var current: Calendar
    private var s: String? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: ItemAdapter? = null
    private lateinit var cat: Array<String>
    private lateinit var repeate: Array<String>
    private var motionLayout: MotionLayout? = null
    private val LOG_TAG = "myLogs"
    private var dbHelper: DBHelper? = null
    private var items = ArrayList<Item>()
    lateinit var fabtoday:FloatingActionButton
    lateinit var fabev:FloatingActionButton




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper:Context = ContextThemeWrapper(activity, R.style.Theme_AppCompat_DayNight_NoActionBar)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        val viewout = localInflater.inflate(R.layout.fragment_main, container, false)
        motionLayout = viewout.findViewById(R.id.motionlt)
        val today = Calendar.getInstance()
        yr = today[Calendar.YEAR]
        month = today[Calendar.MONTH]
        day = today[Calendar.DAY_OF_MONTH]
        current = Calendar.getInstance()
        recyclerView = viewout.findViewById(R.id.rv)
        mcalendarView = viewout.findViewById(R.id.calendarview)
        fabtoday = viewout.findViewById(R.id.today)
        fabtoday.setOnClickListener {
            mcalendarView.date = System.currentTimeMillis()
                current.set(yr, month, day)
                setInitialData(yr, month, day)
        }
        fabev = viewout.findViewById(R.id.createev)
        fabev.setOnClickListener {
                val i = Intent("com.example.CreateEventActivity")
    val extras = Bundle()
    s = ""
    s += if (current.get(Calendar.DAY_OF_MONTH) < 10) "0" + current.get(Calendar.DAY_OF_MONTH) + "." else current.get(
    Calendar.DAY_OF_MONTH
    )
    .toString() + "."
    s += if (current.get(Calendar.MONTH) + 1 < 10) "0" + (current.get(Calendar.MONTH) + 1) + "." else (current.get(
    Calendar.MONTH
    ) + 1).toString() + "."
    s += current.get(Calendar.YEAR)
    extras.putString("date", s)
    extras.putString("from", "main")
    i.putExtras(extras)
    startActivity(i)
        }




        mcalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            current.set(year, month, dayOfMonth)
            setInitialData(year, month, dayOfMonth)
        }



        return viewout
    }
    private fun setInitialData(year1: Int, month1: Int, day1: Int) {
        items.clear()
        var full: String = if (day1 < 10) "0$day1." else "$day1."
        full += if (month1 + 1 < 10) "0" + (month1 + 1) + "." else (month1 + 1).toString() + "."
        full += year1
        dbHelper = DBHelper(context, "String", 1)
        val db = dbHelper!!.writableDatabase
        cat = resources.getStringArray(R.array.notif)
        repeate = resources.getStringArray(R.array.repeating_in)
        motionLayout!!.getTransition(R.id.rvmotion).isEnabled = true
        val c = db.query(
            "events as EV inner join categories as CT on EV.posid = CT._id",
            arrayOf(
                "EV._id",
                "EV.title",
                "EV.timeb",
                "EV.timee",
                "EV.timenotif",
                "EV.data",
                "EV.description",
                "EV.repeating",
                "EV.paral",
                "CT.important"
            ),
            "data = ? OR repeating = ? OR repeating = ? OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? )",
            arrayOf(
                full,
                "Daily",
                "Ежедневно",
                "Weekly",
                "Еженедельно",
                current[Calendar.DAY_OF_WEEK].toString(),
                "Monthly",
                "Ежемесячно",
                current[Calendar.DAY_OF_MONTH].toString(),
                "Yearly",
                "Ежегодно",
                current[Calendar.DAY_OF_MONTH].toString() + "." + (current[Calendar.MONTH] + 1)
            ),
            null,
            null,
            "timeb ASC"
        )
        if (c.moveToFirst()) {
            val dataIndex = c.getColumnIndex(DBHelper.KEY_DATA)
            val idIndex = c.getColumnIndex(DBHelper.KEY_ID)
            val titleIndex = c.getColumnIndex(DBHelper.KEY_TITLE)
            val descIndex = c.getColumnIndex(DBHelper.KEY_DESCRIPTIONS)
            val timebIndex = c.getColumnIndex(DBHelper.KEY_TIMEB)
            val timeeIndex = c.getColumnIndex(DBHelper.KEY_TIMEE)
            val timenotifIndex = c.getColumnIndex(DBHelper.KEY_TIMENOTIF)
            val repeatIndex = c.getColumnIndex(DBHelper.KEY_REPEATING)
            val paralIndex = c.getColumnIndex(DBHelper.KEY_PARAL)
            val impIndex = c.getColumnIndex(DBHelper.KEY_IMP)
            do {
                items.add(
                    Item(
                        c.getString(idIndex),
                        c.getString(titleIndex),
                        c.getString(dataIndex),
                        c.getString(timebIndex),
                        c.getString(timeeIndex),
                        c.getString(timenotifIndex),
                        c.getString(descIndex),
                        c.getString(repeatIndex),
                        c.getString(paralIndex),
                        c.getInt(impIndex)
                    )
                )
            } while (c.moveToNext())
        } else {
            Log.d(LOG_TAG, "0 rows")
            motionLayout!!.getTransition(R.id.rvmotion).isEnabled = false
        }
        c.close()
        buildRV()
    }

    override fun onStart() {
        super.onStart()
        setInitialData(
            current[Calendar.YEAR],
            current[Calendar.MONTH],
            current[Calendar.DAY_OF_MONTH]
        )
    }
    private fun buildRV() {
        adapter = ItemAdapter(context, items, cat, repeate)
        recyclerView!!.adapter = adapter
    }



}