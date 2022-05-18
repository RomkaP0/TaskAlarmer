package com.kradyk.taskalarmer

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.kradyk.taskalarmerimport.DBHelper
import com.kradyk.taskalarmerimport.Item
import java.util.*

class MainActivity : AppCompatActivity() {
    var day = 0
    var month = 0
    var yr = 0
    lateinit var mcalendarView: CalendarView
    var dlg1: CreateEventActivity? = null
    var stn: Settings? = null
    lateinit var current: Calendar
    var s: String? = null
    var recyclerView: RecyclerView? = null
    var adapter: ItemAdapter? = null
    lateinit var cat: Array<String>
    lateinit var repeate: Array<String>
    var motionLayout: MotionLayout? = null
    val LOG_TAG = "myLogs"
    var dbHelper: DBHelper? = null
    lateinit var bottomNavigationView: BottomNavigationView
    var items = ArrayList<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoadPreferences()
        LangLoadPreferences()
        setTitle(R.string.page_title)
        setContentView(R.layout.activity_main)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            val st = Intent("com.example.AuthActivity")
            st.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivityIfNeeded(st, 0)
        }
        motionLayout = findViewById(R.id.motionlt)
        val today = Calendar.getInstance()
        yr = today[Calendar.YEAR]
        month = today[Calendar.MONTH]
        day = today[Calendar.DAY_OF_MONTH]
        current = Calendar.getInstance()
        dlg1 = CreateEventActivity()
        stn = Settings()
        recyclerView = findViewById(R.id.rv)
        mcalendarView = findViewById(R.id.calendarview)
        mcalendarView.setOnDateChangeListener(OnDateChangeListener { calendarView, year, month, dayOfMonth ->
            current.set(year, month, dayOfMonth)
            setInitialData(year, month, dayOfMonth)
        })
        bottomNavigationView = findViewById(R.id.nav)
        bottomNavigationView.clearFocus()
        bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.button1 -> {
                    mcalendarView.date = System.currentTimeMillis()
                    current.set(yr, month, day)
                    setInitialData(yr, month, day)
                }
                R.id.button2 -> {
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
                R.id.button3 -> {
                    val st = Intent("com.example.Settings")
                    startActivity(st)
                }
                R.id.button4 -> {
                    val srch = Intent("com.example.SearchActivity")
                    startActivity(srch)
                }
            }
            false
        })
    }

    private fun setInitialData(year1: Int, month1: Int, day1: Int) {
        items.clear()
        var full: String
        full = if (day1 < 10) "0$day1." else "$day1."
        full += if (month1 + 1 < 10) "0" + (month1 + 1) + "." else (month1 + 1).toString() + "."
        full += year1
        dbHelper = DBHelper(this, "String", 1)
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
            val dataIndex = c.getColumnIndex(DBHelper.Companion.KEY_DATA)
            val idIndex = c.getColumnIndex(DBHelper.Companion.KEY_ID)
            val titleIndex = c.getColumnIndex(DBHelper.Companion.KEY_TITLE)
            val descIndex = c.getColumnIndex(DBHelper.Companion.KEY_DESCRIPTIONS)
            val timebIndex = c.getColumnIndex(DBHelper.Companion.KEY_TIMEB)
            val timeeIndex = c.getColumnIndex(DBHelper.Companion.KEY_TIMEE)
            val timenotifIndex = c.getColumnIndex(DBHelper.Companion.KEY_TIMENOTIF)
            val repeatIndex = c.getColumnIndex(DBHelper.Companion.KEY_REPEATING)
            val paralIndex = c.getColumnIndex(DBHelper.Companion.KEY_PARAL)
            val impIndex = c.getColumnIndex(DBHelper.Companion.KEY_IMP)
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

    public override fun onStart() {
        super.onStart()
        setInitialData(
            current[Calendar.YEAR],
            current[Calendar.MONTH],
            current[Calendar.DAY_OF_MONTH]
        )
    }

    fun buildRV() {
        adapter = ItemAdapter(this, items, cat, repeate)
        recyclerView!!.adapter = adapter
    }

    private fun LoadPreferences() {
        val sharedPreferences = getSharedPreferences(
            "theme", MODE_PRIVATE
        )
        val savedRadioIndex = sharedPreferences.getInt(
            "SAVED_RADIO_BUTTON_INDEX", 0
        )
        when (savedRadioIndex) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> {}
        }
    }

    private fun setLocale(language: String) {
        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        resources.updateConfiguration(configuration, metrics)
        onConfigurationChanged(configuration)
    }

    private fun LangLoadPreferences() {
        val sharedPreferences = getSharedPreferences(
            "lang", MODE_PRIVATE
        )
        val langsavedRadioIndex = sharedPreferences.getInt(
            "LAND_SAVED_RADIO_BUTTON_INDEX", 0
        )
        Log.e("LANG", langsavedRadioIndex.toString())
        when (langsavedRadioIndex) {
            0 -> setLocale("en")
            1 -> setLocale("ru")
            else -> {}
        }
    }

    override fun onRestart() {
        recreate()
        super.onRestart()
    }
}