package com.kradyk.taskalarmer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog


class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView1: RecyclerView
    lateinit var searchAdapter: SearchAdapter
    private var list: ArrayList<SearchItem> = ArrayList()
    private var titlepg:String = ""
    private lateinit var dbHelper: DBHelper
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var selection: String? = null
    private lateinit var selectionArgs: Array<String>
    private var cat: ArrayList<String> = ArrayList()
    private lateinit var database: SQLiteDatabase
    var id = -1
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.search_page)
        setContentView(R.layout.searchactivity)



        val floatingActionButton1 = findViewById<FloatingActionButton>(R.id.srchedit)
        val floatingActionButton2 = findViewById<FloatingActionButton>(R.id.srchdelete)
        val currentNightMode = (resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                floatingActionButton1.backgroundTintList =
                    ColorStateList.valueOf(Color.rgb(251, 251, 251))
                floatingActionButton2.backgroundTintList =
                    ColorStateList.valueOf(Color.rgb(251, 251, 251))
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                floatingActionButton1.backgroundTintList =
                    ColorStateList.valueOf(Color.rgb(40, 40, 40))
                floatingActionButton2.backgroundTintList =
                    ColorStateList.valueOf(Color.rgb(40, 40, 40))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                floatingActionButton1.backgroundTintList =
                    ColorStateList.valueOf(Color.rgb(251, 251, 251))
                floatingActionButton2.backgroundTintList =
                    ColorStateList.valueOf(Color.rgb(251, 251, 251))
            }
        }
        floatingActionButton2.imageTintList = ColorStateList.valueOf(Color.rgb(228, 68, 68))
        autoCompleteTextView = findViewById(R.id.categorysrch)
        dbHelper = DBHelper(this, "String", 1)
        val db = dbHelper.writableDatabase
        val c = db.query("categories", null, null, null, null, null, null)
        if (c.moveToFirst()) {
            val catIndex = c.getColumnIndex(DBHelper.KEY_CAT)
            do {
                cat.add(c.getString(catIndex))
                Log.d("mainLog", c.getString(catIndex))
            } while (c.moveToNext())
        } else Log.d("mainLog", "0 rows")
        c.close()
        autoCompleteTextView.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cat
            )
        )
        autoCompleteTextView.setText("Without", false)
        listSearchView()
        buildRV1()
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                if (autoCompleteTextView.text.toString() == "Without") {
                    floatingActionButton1.visibility = View.GONE
                    floatingActionButton2.visibility = View.GONE
                } else {
                    floatingActionButton1.visibility = View.VISIBLE
                    floatingActionButton2.visibility = View.VISIBLE
                }
                listSearchView()
                buildRV1()
            }
        floatingActionButton1.setOnClickListener { view ->
            val dialog = Dialog(view.context)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.row_add_item)
                    val edittext = dialog.findViewById<EditText>(R.id.edinput)
            edittext.setText(autoCompleteTextView.text.toString())

            val positivebtn = dialog.findViewById<Button>(R.id.positbtn)
                    positivebtn.setOnClickListener {
                        val contentValues = ContentValues()
                        dbHelper = DBHelper(applicationContext, "String", 1)
                        database = dbHelper.writableDatabase
                        contentValues.clear()
                        contentValues.put(
                            DBHelper.KEY_CAT,
                            edittext.text.toString()
                        )
                        database.update(
                            DBHelper.TABLE_CATEGORY,
                            contentValues,
                            "cat = ?",
                            arrayOf(autoCompleteTextView.text.toString())
                        )
                        cat[cat.indexOf(autoCompleteTextView.text.toString())] = edittext.text.toString()
                        autoCompleteTextView.setText(edittext.text.toString(), false)
                        listSearchView()
                        buildRV1()
                    }

            dialog.show()
            //Resources.getSystem().getDisplayMetrics().widthPixels
            dialog.window!!.setLayout((Resources.getSystem().displayMetrics.widthPixels-20),  ViewGroup.LayoutParams.WRAP_CONTENT  )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.window!!.setGravity(Gravity.BOTTOM)

        }
        floatingActionButton2.setOnClickListener { view ->
            val dialog = BottomSheetMaterialDialog.Builder(view.context as Activity)
                .setTitle("Удаление категории")
                .setMessage("Подтвердите удаление категории")
                .setAnimation(R.raw.delete_anim)
                .setPositiveButton("OK") { dialogInterface, _ ->
                    val contentValues = ContentValues()
                    dbHelper = DBHelper(applicationContext, "String", 1)
                    database = dbHelper.writableDatabase
                    val cursor = database.rawQuery(
                        "SELECT * FROM " + DBHelper.TABLE_CATEGORY + " WHERE cat = ?",
                        arrayOf(autoCompleteTextView.text.toString())
                    )
                    if (cursor.moveToFirst()) {
                        id = cursor.getColumnIndex("_id")
                        id = cursor.getInt(id)
                    }
                    cursor.close()
                    database.delete(
                        DBHelper.TABLE_CATEGORY,
                        "cat = ?",
                        arrayOf(autoCompleteTextView.text.toString())
                    )
                    contentValues.clear()
                    contentValues.put(DBHelper.KEY_POSID, 1)
                    val selectionArgs = arrayOf(id.toString())
                    database.update(
                        DBHelper.TABLE_EVENTS,
                        contentValues,
                        "posid = ?",
                        selectionArgs
                    )
                    cat.removeAt(cat.indexOf(autoCompleteTextView.text.toString()))
                    autoCompleteTextView.setText("Without", false)
                    listSearchView()
                    buildRV1()
                    dialogInterface.dismiss()
                }
                .setNegativeButton(view.context.resources.getString(R.string.cancel)){ dialogInterface, _ ->
                    dialogInterface.dismiss()

                }
                .build()
            dialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.appBarSearch)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchAdapter.filter.filter(newText)
                return false
            }
        })
        searchView.setOnCloseListener {
            title = titlepg
            false
        }
        searchView.setOnSearchClickListener {
            titlepg = title.toString()
            title = ""
            listSearchView() }
        return true
    }

    private fun listSearchView() {
        list.clear()
        dbHelper = DBHelper(this, "String", 1)
        val db = dbHelper.writableDatabase
        val table = "events as EV inner join categories as CT on EV.posid = CT._id"
        if (autoCompleteTextView.text.toString() != "Without") {
            selection = "CT.cat = ?"
            selectionArgs = arrayOf(autoCompleteTextView.text.toString())
        } else {
            selection = null
            selectionArgs = emptyArray()
        }
        val c = db.query(table, null, selection, selectionArgs, null, null, "title ASC")
        if (c.moveToFirst()) {
            val dataIndex = c.getColumnIndex(DBHelper.KEY_DATA)
            val idIndex = c.getColumnIndex(DBHelper.KEY_ID)
            val titleIndex = c.getColumnIndex(DBHelper.KEY_TITLE)
            val descIndex = c.getColumnIndex(DBHelper.KEY_DESCRIPTIONS)
            val timebIndex = c.getColumnIndex(DBHelper.KEY_TIMEB)
            val timeeIndex = c.getColumnIndex(DBHelper.KEY_TIMEE)
            val timenotifIndex = c.getColumnIndex(DBHelper.KEY_TIMENOTIF)
            do {
                list.add(
                    SearchItem(
                        c.getString(idIndex),
                        c.getString(titleIndex),
                        c.getString(dataIndex),
                        c.getString(timebIndex),
                        c.getString(timeeIndex),
                        c.getString(timenotifIndex),
                        c.getString(descIndex)
                    )
                )
                Log.d("mainLog", c.getString(idIndex))
            } while (c.moveToNext())
        } else Log.d("mainLog", "0 rows")
        c.close()
        buildRV1()
    }

    private fun buildRV1() {
        recyclerView1 = findViewById(R.id.rv1)
        searchAdapter = SearchAdapter(this, list)
        recyclerView1.adapter = searchAdapter
    }
}