@file:Suppress("PropertyName")

package com.kradyk.taskalarmer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var day = 0
    private var month = 0
    private var yr = 0
    private lateinit var mcalendarView: CalendarView
    private var dlg1: CreateEventActivity? = null
    private var stn: Settings? = null
    private lateinit var current: Calendar
    private var s: String? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: ItemAdapter? = null
    private lateinit var cat: Array<String>
    private lateinit var repeate: Array<String>
    private var motionLayout: MotionLayout? = null
    private val LOG_TAG = "myLogs"
    private var dbHelper: DBHelper? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    private var items = ArrayList<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadPreferences()
        langLoadPreferences()
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
        mcalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            current.set(year, month, dayOfMonth)
            setInitialData(year, month, dayOfMonth)
        }
        bottomNavigationView = findViewById(R.id.nav)
        bottomNavigationView.clearFocus()
        bottomNavigationView.setOnItemSelectedListener { item ->
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
        }
    }

    private fun setInitialData(year1: Int, month1: Int, day1: Int) {
        items.clear()
        var full: String = if (day1 < 10) "0$day1." else "$day1."
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

    public override fun onStart() {
        super.onStart()
        setInitialData(
            current[Calendar.YEAR],
            current[Calendar.MONTH],
            current[Calendar.DAY_OF_MONTH]
        )
    }

    private fun buildRV() {
        adapter = ItemAdapter(this, items, cat, repeate)
        recyclerView!!.adapter = adapter
    }

    private fun loadPreferences() {
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

    private fun langLoadPreferences() {
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