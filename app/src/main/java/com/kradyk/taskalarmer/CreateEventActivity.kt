package com.kradyk.taskalarmer

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class CreateEventActivity : AppCompatActivity() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var editText2: EditText
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var autoCompleteTextView1: AutoCompleteTextView
    private lateinit var autoCompleteTextView2: AutoCompleteTextView
    private lateinit var autoCompleteTextView3: AutoCompleteTextView
    private lateinit var datetext:TextView
    lateinit var iconback:ImageButton
    lateinit var iconsubmit:ImageButton

    private var hour: Int = 0
    private var minute: Int = 0
    private var cat: ArrayList<String> = ArrayList()
    private var catid: ArrayList<Int> = ArrayList()
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null
    private var timeNotifMillis: Long = 0
    var c: Int = 0
    private var scrollView: ScrollView? = null
    private var fillItem: FillItem? = null
    private lateinit var data: String
    private var fillItems: ArrayList<FillItem> = ArrayList()
    private lateinit var cal: Calendar
    private var lastId: String? = null
    private var switchvalue: String = "0"
    private var curID: Int = -1
    private var timebint: Int = 0
    private var timeeint: Int = 0
    private var builder: MaterialTimePicker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .setTitleText("Укажите время")
        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
        .build()
    private var builderend: MaterialTimePicker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .setTitleText("Укажите время")
        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
        .build()
    private var builderdate: MaterialDatePicker.Builder<Long>? = null
    private var datePicker: MaterialDatePicker<Long>? = null
    private lateinit var dbHelper: DBHelper
    private lateinit var database: SQLiteDatabase
    private var count: Int = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_event_activity)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView1)
        datetext = findViewById(R.id.dateev)
        iconback = findViewById(R.id.iconnav)
        iconsubmit = findViewById(R.id.okev)

        if ((autoCompleteTextView.hint.toString() == "Название")) datetext.text =
            "Дата : " + intent.extras!!
                .getString("date", "-1") else datetext.text = "Date : " + intent.extras!!
            .getString("date", "-1")
        data = intent.extras!!.getString("date", "-1")
        setListFills()
        createNotificationChannel()
        val cal: Calendar = Calendar.getInstance()
        val switcher: SwitchCompat = findViewById(R.id.paralsw)
        switcher.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            switchvalue = if (b) {
                "1"
            } else "0"
        }
        editText2 = findViewById(R.id.edtxt2)
        val notifybef: Array<String> = resources.getStringArray(R.array.notif)
        autoCompleteTextView1 = findViewById(R.id.notifybefore)
        autoCompleteTextView1.setText(notifybef[0])
        autoCompleteTextView1.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                notifybef
            )
        )
        autoCompleteTextView.threshold = 2
        val fillAdapter = FillAdapter(this, R.layout.autolist, fillItems)
        autoCompleteTextView.setAdapter(fillAdapter)
        autoCompleteTextView.onItemClickListener =
            OnItemClickListener { adapterView, _, i, _ ->
                fillItem = adapterView.getItemAtPosition(i) as FillItem?
                button1.text = fillItem!!.timeb
                button2.text = fillItem!!.timee
                autoCompleteTextView1.setText(fillItem!!.timenotif, false)
            }
        val repeating: Array<String> = resources.getStringArray(R.array.repeating_in)
        autoCompleteTextView2 = findViewById(R.id.repeating)
        autoCompleteTextView2.setText(repeating[0])
        autoCompleteTextView2.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                repeating
            )
        )
        dbHelper = DBHelper(this, "String", 1)
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val c: Cursor = db.query("categories", null, null, null, null, null, null)
        if (c.moveToFirst()) {
            val catIndex: Int = c.getColumnIndex(DBHelper.KEY_CAT)
            do {
                cat.add(c.getString(catIndex))
                Log.d("mainLog", c.getString(catIndex))
            } while (c.moveToNext())
        } else Log.d("mainLog", "0 rows")
        c.close()
        cat.add("Добавить")
        dbHelper = DBHelper(this, "String", 1)
        database = dbHelper.writableDatabase
        val contentValues = ContentValues()
        autoCompleteTextView3 = findViewById(R.id.category)
        autoCompleteTextView3.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cat
            )
        )
        autoCompleteTextView3.setText("Without", false)
        autoCompleteTextView3.onItemClickListener =
            OnItemClickListener { adapterView, view, i, _ ->
                if ((adapterView.getItemAtPosition(i) == "Добавить")) {
                    val inputEditTextField = EditText(view.context)
                    val dialog: AlertDialog = AlertDialog.Builder(view.context)
                        .setTitle("Введите название категории")
                        .setView(inputEditTextField)
                        .setPositiveButton("OK"
                        ) { _, _ ->
                            var c = 0
                            for (j: Any in cat) {
                                val k: String = j as String
                                if ((k.lowercase(Locale.getDefault())
                                        .trim { it <= ' ' } == inputEditTextField.text
                                        .toString().lowercase(
                                            Locale.getDefault()
                                        ).trim { it <= ' ' })
                                ) c += 1
                            }
                            if (c == 0) {
                                cat.add(cat.size - 1, inputEditTextField.text.toString())
                                autoCompleteTextView3.setText(
                                    cat[cat.size - 2],
                                    false
                                )
                                contentValues.clear()
                                contentValues.put(DBHelper.KEY_IMP, 1)
                                contentValues.put(
                                    DBHelper.KEY_CAT,
                                    cat[cat.size - 2]
                                )
                                database.insert(
                                    DBHelper.TABLE_CATEGORY,
                                    null,
                                    contentValues
                                )
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Already exist",
                                    Toast.LENGTH_SHORT
                                ).show()
                                autoCompleteTextView3.setText("Without", false)
                            }
                        }
                        .setNegativeButton("Cancel"
                        ) { _, _ -> autoCompleteTextView3.setText("Without", false) }
                        .create()
                    dialog.show()
                }
            }
        scrollView = findViewById(R.id.scrollviewd1)
        builderdate = MaterialDatePicker.Builder.datePicker()
        builderdate!!.setTitleText("Укажите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.Theme_App_Calendar)
        datePicker = builderdate!!.build()
        button1 = findViewById(R.id.btn1)
        button2 = findViewById(R.id.btn2)
        val button3: Button = findViewById(R.id.imb3)
        if ((intent.extras!!.getString("from") == "rv")) {
            autoCompleteTextView.setText(intent.extras!!.getString("title"))
            button1.text = intent.extras!!.getString("timeb")
            button2.text = intent.extras!!.getString("timee")
            editText2.setText(intent.extras!!.getString("desc"))
            autoCompleteTextView1.setText(intent.extras!!.getString("notify"), false)
            autoCompleteTextView2.setText(intent.extras!!.getString("repeat"), false)
            if ((intent.extras!!.getString("paral") == "1")) switcher.isChecked = true
        }
        val switchCompat: SwitchCompat = findViewById(R.id.switch1)
        switchCompat.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isChecked) {
                button1.isClickable = false
                button2.isClickable = false
                button1.text = "00:00"
                timebint = 0
                button2.text = "23:59"
                timeeint = 2359
            } else {
                button1.isClickable = true
                button2.isClickable = true
            }
        }
        button1.setOnClickListener {
            builder.show(supportFragmentManager, "time")
            builder.addOnPositiveButtonClickListener {
                hour = builder.hour
                minute = builder.minute
                if (hour < 10) button1.text = "0$hour:" else button1.text =
                    "$hour:"
                timebint = hour * 100
                if (minute < 10) button1.text =
                    button1.text.toString() + "0" + minute else button1.text =
                    button1.text.toString() + minute
                timebint += minute
                timeeint = timebint
                button2.text = button1.text.toString()
            }
        }
        button2.setOnClickListener {
            if ((button1.text.toString() == "Время начала")) {
                Toast.makeText(
                    this@CreateEventActivity,
                    "Укажите начальное время",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                builderend.show(supportFragmentManager, "time")
                builderend.addOnPositiveButtonClickListener {
                    if (builderend.hour > hour) {
                        if (builderend.hour < 10) button2.text =
                            "0" + builderend.hour + ":" else button2.text =
                            builderend.hour.toString() + ":"
                        timeeint = builderend.hour * 100
                        if (builderend.minute < 10) button2.text =
                            button2.text.toString() + "0" + builderend.minute else button2.text =
                            button2.text.toString() + builderend.minute
                        timeeint += builderend.minute
                    } else if (builderend.hour == hour) {
                        if (builderend.minute >= minute) {
                            if (builderend.hour < 10) button2.text =
                                "0" + builderend.hour + ":" else button2.text =
                                builderend.hour.toString() + ":"
                            timeeint = builderend.hour * 100
                            if (builderend.minute < 10) button2.text =
                                button2.text.toString() + "0" + builderend.minute else button2.text =
                                button2.text.toString() + builderend.minute
                            timeeint += builderend.minute
                        }
                    }
                }
            }
        }
        button3.setOnClickListener {
            datePicker!!.show(supportFragmentManager, "date")
            datePicker!!.addOnPositiveButtonClickListener { selection ->
                cal.timeInMillis = (selection)!!
                data = ""
                data += if ((cal.get(Calendar.DAY_OF_MONTH)) < 10) "0" + cal.get(Calendar.DAY_OF_MONTH) + "." else (cal.get(
                    Calendar.DAY_OF_MONTH
                )).toString() + "."
                data += if ((cal.get(Calendar.MONTH) + 1) < 10) "0" + (cal.get(Calendar.MONTH) + 1) + "." else (cal.get(
                    Calendar.MONTH
                ) + 1).toString() + "."
                data += cal.get(Calendar.YEAR)
                datetext.text = "Дата : $data"
            }
        }
        iconsubmit.setOnClickListener {
                if ((autoCompleteTextView.text.toString() != "") && (button1.text
                        .toString() != "Начало") && (button2.text
                        .toString() != "Окончание") && (button1.text
                        .toString() != "Initial time") && (button2.text
                        .toString() != "End time")
                ) {
                    val caldate: Array<String> = data.split(".").toTypedArray()
                    val caltime: Array<String> =
                        button1.text.toString().split(":").toTypedArray()
                    cal.set(
                        caldate[2].toInt(),
                        caldate[1].toInt() - 1,
                        caldate[0].toInt(),
                        caltime[0].toInt(),
                        caltime[1].toInt(),
                        0
                    )
                    if ((switchvalue == "0")) {
                        val cursor: Cursor = database.query(
                            "events",
                            null,
                            "(data = ? OR repeating = ? OR repeating = ? OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? )) AND paral = ? AND ((timebint <= ? AND timeeint >= ?) OR (timebint <= ? AND timeeint >= ?)OR (timebint >= ? AND timeeint <= ?)) ",
                            arrayOf(
                                data,
                                "Daily",
                                "Ежедневно",
                                "Weekly",
                                "Еженедельно",
                                cal.get(
                                    Calendar.DAY_OF_WEEK
                                ).toString(),
                                "Monthly",
                                "Ежемесячно",
                                caldate[0],
                                "Yearly",
                                "Ежегодно",
                                caldate[0] + "." + caldate[1],
                                "1",
                                timebint.toString(),
                                timebint.toString(),
                                timeeint.toString(),
                                timeeint.toString(),
                                timebint.toString(),
                                timeeint.toString()
                            ),
                            null,
                            null,
                            "timeb ASC"
                        )
                        if (cursor.moveToFirst()) {
                            if ((intent.extras!!.getString("from") == "rv")) {
                                curID = intent.extras!!.getString("id")!!.toInt()
                            }
                            do {
                                val idIndex: Int =
                                    cursor.getColumnIndex(DBHelper.KEY_ID)
                                println(cursor.getString(idIndex).toInt())
                                if (curID == cursor.getString(idIndex).toInt()) continue
                                count += 1
                            } while (cursor.moveToNext())
                        } else {
                            Log.d("Flog", "No")
                        }
                        cursor.close()
                    } else {
                        val cursor: Cursor = database.query(
                            "events",
                            null,
                            "(data = ? OR repeating = ? OR repeating = ? OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? )) AND ((timebint <= ? AND timeeint >= ?) OR (timebint <= ? AND timeeint >= ?)OR (timebint >= ? AND timeeint <= ?)) ",
                            arrayOf(
                                data,
                                "Daily",
                                "Ежедневно",
                                "Weekly",
                                "Еженедельно",
                                cal.get(
                                    Calendar.DAY_OF_WEEK
                                ).toString(),
                                "Monthly",
                                "Ежемесячно",
                                cal.get(
                                    Calendar.DAY_OF_MONTH
                                ).toString(),
                                "Yearly",
                                "Ежегодно",
                                cal.get(
                                    Calendar.DAY_OF_MONTH
                                ).toString() + "." + (cal.get(Calendar.MONTH) + 1),
                                timebint.toString(),
                                timebint.toString(),
                                timeeint.toString(),
                                timeeint.toString(),
                                timebint.toString(),
                                timeeint.toString()
                            ),
                            null,
                            null,
                            "timeb ASC"
                        )
                        if (cursor.moveToFirst()) {
                            if ((intent.extras!!.getString("from") == "rv")) {
                                curID = intent.extras!!.getString("id")!!.toInt()
                            }
                            do {
                                val idIndex: Int =
                                    cursor.getColumnIndex(DBHelper.KEY_ID)
                                println(cursor.getString(idIndex).toInt())
                                if (curID == cursor.getString(idIndex).toInt()) continue
                                count += 1
                            } while (cursor.moveToNext())
                        } else {
                            Log.d("Flog", "No")
                        }
                        cursor.close()
                    }
                    if ((intent.extras!!.getString("from") == "main")) {
                        if (count == 0) {
                            try {
                                if ((fillItem!!.title != autoCompleteTextView.text
                                        .toString() ||
                                            fillItem!!.timeb != button1.text
                                        .toString() ||
                                            fillItem!!.timee != button2.text
                                        .toString())
                                ) {
                                    contentValues.clear()
                                    contentValues.put(
                                        DBHelper.KEY_TITLE,
                                        autoCompleteTextView.text.toString()
                                    )
                                    contentValues.put(
                                        DBHelper.KEY_TIMEB,
                                        button1.text.toString()
                                    )
                                    contentValues.put(
                                        DBHelper.KEY_TIMEE,
                                        button2.text.toString()
                                    )
                                    contentValues.put(
                                        DBHelper.KEY_TIMENOTIF,
                                        autoCompleteTextView1.text.toString()
                                    )
                                    setTimeNotifMillis()
                                    contentValues.put(
                                        DBHelper.KEY_TIMENOTIFMILLIS,
                                        timeNotifMillis
                                    )
                                    catid.clear()
                                    catid.add(0)
                                    val c: Cursor = db.query(
                                        "categories",
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    )
                                    if (c.moveToFirst()) {
                                        val cat_ID =
                                            c.getColumnIndex(DBHelper.KEY_ID)
                                        val catIndex: Int =
                                            c.getColumnIndex(DBHelper.KEY_CAT)
                                        do {
                                            catid.add(
                                                cat.indexOf(c.getString(catIndex)),
                                                c.getString(cat_ID).toInt()
                                            )
                                        } while (c.moveToNext())
                                    } else Log.d("mainLog", "0 rows")
                                    c.close()
                                    database.insert(
                                        DBHelper.TABLE_FILLS,
                                        null,
                                        contentValues
                                    )
                                    Log.d("ExcFillInsertLog", "Exception")
                                    contentValues.put(
                                        DBHelper.KEY_TIMEBMILLIS,
                                        cal.timeInMillis
                                    )
                                    contentValues.put(DBHelper.KEY_PARAL, switchvalue)
                                    contentValues.put(DBHelper.KEY_DATA, data)
                                    contentValues.put(
                                        DBHelper.KEY_DESCRIPTIONS,
                                        editText2.text.toString()
                                    )
                                    contentValues.put(DBHelper.KEY_TIMEBINT, timebint)
                                    contentValues.put(DBHelper.KEY_TIMEEINT, timeeint)
                                    contentValues.put(
                                        DBHelper.KEY_REPEATING,
                                        autoCompleteTextView2.text.toString()
                                    )
                                    if ((autoCompleteTextView2.text
                                            .toString() == "Еженедельно") || (autoCompleteTextView2.text
                                            .toString() == "Weekly")
                                    ) contentValues.put(
                                        DBHelper.KEY_INTERVAL, cal.get(
                                            Calendar.DAY_OF_WEEK
                                        )
                                    ) else if ((autoCompleteTextView2.text
                                            .toString() == "Ежемесячно") || (autoCompleteTextView2.text
                                            .toString() == "Monthly")
                                    ) contentValues.put(
                                        DBHelper.KEY_INTERVAL, cal.get(
                                            Calendar.DAY_OF_MONTH
                                        )
                                    ) else if ((autoCompleteTextView2.text
                                            .toString() == "Ежегодно") || (autoCompleteTextView2.text
                                            .toString() == "Yearly")
                                    ) contentValues.put(
                                        DBHelper.KEY_INTERVAL, cal.get(
                                            Calendar.DAY_OF_MONTH
                                        ).toString() + "." + (cal.get(
                                            Calendar.MONTH
                                        ) + 1)
                                    )
                                    contentValues.put(
                                        DBHelper.KEY_POSID,
                                        catid[cat.indexOf(
                                            autoCompleteTextView3.text.toString()
                                        )] as Int?
                                    )
                                    database.insert(
                                        DBHelper.TABLE_EVENTS,
                                        null,
                                        contentValues
                                    )
                                    setAlarm()
                                } else {
                                    contentValues.clear()
                                    contentValues.put(
                                        DBHelper.KEY_TITLE,
                                        autoCompleteTextView.text.toString()
                                    )
                                    contentValues.put(
                                        DBHelper.KEY_TIMEB,
                                        button1.text.toString()
                                    )
                                    contentValues.put(
                                        DBHelper.KEY_TIMEE,
                                        button2.text.toString()
                                    )
                                    contentValues.put(
                                        DBHelper.KEY_TIMENOTIF,
                                        autoCompleteTextView1.text.toString()
                                    )
                                    setTimeNotifMillis()
                                    contentValues.put(
                                        DBHelper.KEY_TIMENOTIFMILLIS,
                                        timeNotifMillis
                                    )
                                    catid.clear()
                                    catid.add(0)
                                    val c: Cursor = db.query(
                                        "categories",
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    )
                                    if (c.moveToFirst()) {
                                        val cat_ID: Int =
                                            c.getColumnIndex(DBHelper.KEY_ID)
                                        val catIndex: Int =
                                            c.getColumnIndex(DBHelper.KEY_CAT)
                                        do {
                                            catid.add(
                                                cat.indexOf(c.getString(catIndex)),
                                                c.getString(cat_ID).toInt()
                                            )
                                        } while (c.moveToNext())
                                    } else Log.d("mainLog", "0 rows")
                                    c.close()
                                    contentValues.put(
                                        DBHelper.KEY_POSID,
                                        catid[cat.indexOf(
                                            autoCompleteTextView3.text.toString()
                                        )] as Int?
                                    )
                                    contentValues.put(DBHelper.KEY_DATA, data)
                                    contentValues.put(
                                        DBHelper.KEY_DESCRIPTIONS,
                                        editText2.text.toString()
                                    )
                                    contentValues.put(DBHelper.KEY_PARAL, switchvalue)
                                    contentValues.put(
                                        DBHelper.KEY_TIMEBMILLIS,
                                        cal.timeInMillis
                                    )
                                    contentValues.put(DBHelper.KEY_TIMEBINT, timebint)
                                    contentValues.put(DBHelper.KEY_TIMEEINT, timeeint)
                                    contentValues.put(
                                        DBHelper.KEY_REPEATING,
                                        autoCompleteTextView2.text.toString()
                                    )
                                    if ((autoCompleteTextView2.text
                                            .toString() == "Еженедельно") || (autoCompleteTextView2.text
                                            .toString() == "Weekly")
                                    ) contentValues.put(
                                        DBHelper.KEY_INTERVAL, cal.get(
                                            Calendar.DAY_OF_WEEK
                                        )
                                    ) else if ((autoCompleteTextView2.text
                                            .toString() == "Ежемесячно") || (autoCompleteTextView2.text
                                            .toString() == "Monthly")
                                    ) contentValues.put(
                                        DBHelper.KEY_INTERVAL, cal.get(
                                            Calendar.DAY_OF_MONTH
                                        )
                                    ) else if ((autoCompleteTextView2.text
                                            .toString() == "Ежегодно") || (autoCompleteTextView2.text
                                            .toString() == "Yearly")
                                    ) contentValues.put(
                                        DBHelper.KEY_INTERVAL, cal.get(
                                            Calendar.DAY_OF_MONTH
                                        ).toString() + "." + (cal.get(
                                            Calendar.MONTH
                                        ) + 1)
                                    )
                                    database.insert(
                                        DBHelper.TABLE_EVENTS,
                                        null,
                                        contentValues
                                    )
                                    setAlarm()
                                }
                                Log.d("FillInsertLog", "fill")
                            } catch (e: NullPointerException) {
                                contentValues.clear()
                                contentValues.put(
                                    DBHelper.KEY_TITLE,
                                    autoCompleteTextView.text.toString()
                                )
                                contentValues.put(
                                    DBHelper.KEY_TIMEB,
                                    button1.text.toString()
                                )
                                contentValues.put(
                                    DBHelper.KEY_TIMEE,
                                    button2.text.toString()
                                )
                                contentValues.put(
                                    DBHelper.KEY_TIMENOTIF,
                                    autoCompleteTextView1.text.toString()
                                )
                                setTimeNotifMillis()
                                contentValues.put(
                                    DBHelper.KEY_TIMENOTIFMILLIS,
                                    timeNotifMillis
                                )
                                catid.clear()
                                val c: Cursor =
                                    db.query("categories", null, null, null, null, null, null)
                                if (c.moveToFirst()) {
                                    val cat_ID: Int =
                                        c.getColumnIndex(DBHelper.KEY_ID)
                                    val catIndex: Int =
                                        c.getColumnIndex(DBHelper.KEY_CAT)
                                    do {
                                        catid.add(
                                            cat.indexOf(c.getString(catIndex)),
                                            c.getString(cat_ID).toInt()
                                        )
                                    } while (c.moveToNext())
                                } else Log.d("mainLog", "0 rows")
                                c.close()
                                database.insert(
                                    DBHelper.TABLE_FILLS,
                                    null,
                                    contentValues
                                )
                                Log.d("ExcFillInsertLog", "Exception")
                                val find: Int = catid[cat.indexOf(
                                    autoCompleteTextView3.text.toString()
                                )]
                                contentValues.put(DBHelper.KEY_POSID, find)
                                contentValues.put(DBHelper.KEY_DATA, data)
                                contentValues.put(DBHelper.KEY_PARAL, switchvalue)
                                contentValues.put(
                                    DBHelper.KEY_DESCRIPTIONS,
                                    editText2.text.toString()
                                )
                                contentValues.put(
                                    DBHelper.KEY_TIMEBMILLIS,
                                    cal.timeInMillis
                                )
                                contentValues.put(DBHelper.KEY_TIMEBINT, timebint)
                                contentValues.put(DBHelper.KEY_TIMEEINT, timeeint)
                                contentValues.put(
                                    DBHelper.KEY_REPEATING,
                                    autoCompleteTextView2.text.toString()
                                )
                                if ((autoCompleteTextView2.text
                                        .toString() == "Еженедельно") || (autoCompleteTextView2.text
                                        .toString() == "Weekly")
                                ) contentValues.put(
                                    DBHelper.KEY_INTERVAL, cal.get(
                                        Calendar.DAY_OF_WEEK
                                    )
                                ) else if ((autoCompleteTextView2.text
                                        .toString() == "Ежемесячно") || (autoCompleteTextView2.text
                                        .toString() == "Monthly")
                                ) contentValues.put(
                                    DBHelper.KEY_INTERVAL, cal.get(
                                        Calendar.DAY_OF_MONTH
                                    )
                                ) else if ((autoCompleteTextView2.text
                                        .toString() == "Ежегодно") || (autoCompleteTextView2.text
                                        .toString() == "Yearly")
                                ) contentValues.put(
                                    DBHelper.KEY_INTERVAL, cal.get(
                                        Calendar.DAY_OF_MONTH
                                    ).toString() + "." + (cal.get(Calendar.MONTH) + 1)
                                )
                                database.insert(
                                    DBHelper.TABLE_EVENTS,
                                    null,
                                    contentValues
                                )
                                setAlarm()
                            }
                            finish()
                        } else {
                            count = 0
                            Toast.makeText(
                                baseContext,
                                "Указанное время занято",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        if (count == 0) {
                            contentValues.clear()
                            contentValues.put(
                                DBHelper.KEY_TITLE,
                                autoCompleteTextView.text.toString()
                            )
                            contentValues.put(
                                DBHelper.KEY_TIMEB,
                                button1.text.toString()
                            )
                            contentValues.put(
                                DBHelper.KEY_TIMEE,
                                button2.text.toString()
                            )
                            contentValues.put(DBHelper.KEY_DATA, data)
                            contentValues.put(
                                DBHelper.KEY_TIMENOTIF,
                                autoCompleteTextView1.text.toString()
                            )
                            catid.clear()
                            catid.add(0)
                            val c: Cursor =
                                db.query("categories", null, null, null, null, null, null)
                            if (c.moveToFirst()) {
                                val cat_ID: Int = c.getColumnIndex(DBHelper.KEY_ID)
                                val catIndex: Int = c.getColumnIndex(DBHelper.KEY_CAT)
                                do {
                                    catid.add(
                                        cat.indexOf(c.getString(catIndex)),
                                        c.getString(cat_ID).toInt()
                                    )
                                } while (c.moveToNext())
                            } else Log.d("mainLog", "0 rows")
                            c.close()
                            contentValues.put(
                                DBHelper.KEY_POSID,
                                catid[cat.indexOf(
                                    autoCompleteTextView3.text.toString()
                                )] as Int?
                            )
                            setTimeNotifMillis()
                            contentValues.put(
                                DBHelper.KEY_TIMENOTIFMILLIS,
                                timeNotifMillis
                            )
                            contentValues.put(
                                DBHelper.KEY_DESCRIPTIONS,
                                editText2.text.toString()
                            )
                            contentValues.put(DBHelper.KEY_PARAL, switchvalue)
                            contentValues.put(
                                DBHelper.KEY_TIMEBMILLIS,
                                cal.timeInMillis
                            )
                            contentValues.put(
                                DBHelper.KEY_REPEATING,
                                autoCompleteTextView2.text.toString()
                            )
                            contentValues.put(DBHelper.KEY_TIMEBINT, timebint)
                            contentValues.put(DBHelper.KEY_TIMEEINT, timeeint)
                            if ((autoCompleteTextView2.text
                                    .toString() == "Нет") || (autoCompleteTextView2.text
                                    .toString() == "No")
                            ) contentValues.put(
                                DBHelper.KEY_INTERVAL,
                                null as String?
                            ) else if ((autoCompleteTextView2.text
                                    .toString() == "Ежедневно") || (autoCompleteTextView2.text
                                    .toString() == "Daily")
                            ) contentValues.put(
                                DBHelper.KEY_INTERVAL,
                                null as String?
                            ) else if ((autoCompleteTextView2.text
                                    .toString() == "Еженедельно") || (autoCompleteTextView2.text
                                    .toString() == "Weekly")
                            ) contentValues.put(
                                DBHelper.KEY_INTERVAL, cal.get(
                                    Calendar.DAY_OF_WEEK
                                )
                            ) else if ((autoCompleteTextView2.text
                                    .toString() == "Ежемесячно") || (autoCompleteTextView2.text
                                    .toString() == "Monthly")
                            ) contentValues.put(
                                DBHelper.KEY_INTERVAL, cal.get(
                                    Calendar.DAY_OF_MONTH
                                )
                            ) else if ((autoCompleteTextView2.text
                                    .toString() == "Ежегодно") || (autoCompleteTextView2.text
                                    .toString() == "Yearly")
                            ) contentValues.put(
                                DBHelper.KEY_INTERVAL, cal.get(
                                    Calendar.DAY_OF_MONTH
                                ).toString() + "." + (cal.get(Calendar.MONTH) + 1)
                            )
                            database.update(
                                DBHelper.TABLE_EVENTS,
                                contentValues,
                                "_id = ?",
                                arrayOf(
                                    intent.extras!!.getString("id")
                                )
                            )
                            setAlarm()
                            finish()
                        } else {
                            count = 0
                            Toast.makeText(
                                baseContext,
                                "Указанное время занято",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Введите недостающие данные",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        iconback.setOnClickListener{
            finish()
        }

        }


    @SuppressLint("UnspecifiedImmutableFlag", "Recycle")
    private fun setAlarm() {
        val caldate: Array<String> = data.split(".").toTypedArray()
        val caltime: Array<String> = button1.text.toString().split(":").toTypedArray()
        cal = Calendar.getInstance()
        cal.set(
            caldate[2].toInt(),
            caldate[1].toInt() - 1,
            caldate[0].toInt(),
            caltime[0].toInt(),
            caltime[1].toInt(),
            0
        )
        if ((intent.extras!!.getString("from") == "main")) {
            val cursor: Cursor? = database.query(
                DBHelper.TABLE_EVENTS,
                arrayOf("_id"),
                null,
                null,
                null,
                null,
                "_id DESC",
                "1"
            )
            if (cursor != null && cursor.moveToFirst()) {
                lastId = cursor.getString(0)
            }
            cursor!!.close()
        } else {
            lastId = intent.extras!!.getString("id")
        }
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager?
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("NOTIFICATION_ID", lastId!!.toInt())
        intent.putExtra("TITLE_TEXT", autoCompleteTextView.text.toString())
        intent.putExtra("BIG_TEXT", editText2.text.toString())
        intent.putExtra("TIMENOTIF", (cal.timeInMillis - timeNotifMillis))
        intent.putExtra("REPEATING", autoCompleteTextView2.text.toString())
        pendingIntent = PendingIntent.getBroadcast(
            this,
            lastId!!.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager!!.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            (cal.timeInMillis - timeNotifMillis),
            pendingIntent
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "MyReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("OrgNotCll", name, importance)
            channel.description = description
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 400, 200, 400)
            val notificationManager: NotificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setListFills() {
        fillItems.clear()
        dbHelper = DBHelper(this, "String", 1)
        database = dbHelper.writableDatabase
        val c: Cursor = database.query(
            "fills",
            arrayOf("_id", "title", "timeb", "timee", "timenotif"),
            null,
            null,
            null,
            null,
            "title ASC"
        )
        if (c.moveToFirst()) {
            val idIndex: Int = c.getColumnIndex(DBHelper.KEY_ID)
            val titleIndex: Int = c.getColumnIndex(DBHelper.KEY_TITLE)
            val timebIndex: Int = c.getColumnIndex(DBHelper.KEY_TIMEB)
            val timeeIndex: Int = c.getColumnIndex(DBHelper.KEY_TIMEE)
            val timenotifIndex: Int = c.getColumnIndex(DBHelper.KEY_TIMENOTIF)
            do {
                fillItems.add(
                    FillItem(
                        c.getString(idIndex),
                        c.getString(titleIndex),
                        c.getString(timebIndex),
                        c.getString(timeeIndex),
                        c.getString(timenotifIndex)
                    )
                )
            } while (c.moveToNext())
        } else Log.d("d1Log", "0 rows")
        c.close()
    }

    private fun setTimeNotifMillis() {
        if ((autoCompleteTextView1.text
                .toString() == "At moment") || (autoCompleteTextView1.text
                .toString() == "В момент")
        ) timeNotifMillis = 0 else if ((autoCompleteTextView1.text
                .toString() == "5 minutes") || (autoCompleteTextView1.text
                .toString() == "5 минут")
        ) timeNotifMillis = 300000 else if ((autoCompleteTextView1.text
                .toString() == "10 minutes") || (autoCompleteTextView1.text
                .toString() == "10 минут")
        ) timeNotifMillis = 600000 else if ((autoCompleteTextView1.text
                .toString() == "30 minutes") || (autoCompleteTextView1.text
                .toString() == "30 минут")
        ) timeNotifMillis = 1800000 else if ((autoCompleteTextView1.text
                .toString() == "1 hour") || (autoCompleteTextView1.text
                .toString() == "1 час")
        ) timeNotifMillis = 3600000 else if ((autoCompleteTextView1.text
                .toString() == "6 hours") || (autoCompleteTextView1.text
                .toString() == "6 часов")
        ) timeNotifMillis = 21600000 else if ((autoCompleteTextView1.text
                .toString() == "12 hours") || (autoCompleteTextView1.text
                .toString() == "12 часов")
        ) timeNotifMillis = 43200000 else if ((autoCompleteTextView1.text
                .toString() == "24 hours") || (autoCompleteTextView1.text
                .toString() == "24 часа")
        ) timeNotifMillis = 86400000
    }
}