package com.kradyk.taskalarmer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kradyk.taskalarmerimport.DBHelper
import com.kradyk.taskalarmerimport.SearchItem
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog


class SearchActivity : AppCompatActivity() {
    lateinit var recyclerView1: RecyclerView
    lateinit var searchAdapter: SearchAdapter
    var list: ArrayList<SearchItem> = ArrayList()
    var titlepg:String = ""
    lateinit var dbHelper: DBHelper
    lateinit var autoCompleteTextView: AutoCompleteTextView
    var selection: String? = null
    lateinit var selectionArgs: Array<String>
    var cat: ArrayList<String> = ArrayList()
    lateinit var database: SQLiteDatabase
    var id = -1
    @SuppressLint("ClickableViewAccessibility")
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
            val catIndex = c.getColumnIndex(DBHelper.Companion.KEY_CAT)
            do {
                cat.add(c.getString(catIndex))
                Log.d("mainLog", c.getString(catIndex))
            } while (c.moveToNext())
        } else Log.d("mainLog", "0 rows")
        c.close()
        autoCompleteTextView.setAdapter<ArrayAdapter<String>>(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                cat
            )
        )
        autoCompleteTextView.setText("Without", false)
        listSearchView()
        buildRV1()
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
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
            val inputEditTextField = EditText(view.context)
            inputEditTextField.setText(autoCompleteTextView.text.toString())
            val dialog = AlertDialog.Builder(view.context)
                .setTitle("Введите название категории")
                .setView(inputEditTextField)
                .setPositiveButton("OK") { dialogInterface, i ->
                    val contentValues = ContentValues()
                    dbHelper = DBHelper(applicationContext, "String", 1)
                    database = dbHelper.writableDatabase
                    contentValues.clear()
                    contentValues.put(
                        DBHelper.Companion.KEY_CAT,
                        inputEditTextField.text.toString()
                    )
                    database.update(
                        DBHelper.Companion.TABLE_CATEGORY,
                        contentValues,
                        "cat = ?",
                        arrayOf(autoCompleteTextView.text.toString())
                    )
                    cat.set(
                        cat.indexOf(autoCompleteTextView.text.toString()),
                        inputEditTextField.text.toString()
                    )
                    autoCompleteTextView.setText(inputEditTextField.text.toString(), false)
                    listSearchView()
                    buildRV1()
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
            dialog.show()
        }
        floatingActionButton2.setOnClickListener { view ->
            val dialog = BottomSheetMaterialDialog.Builder(view.context as Activity)
                .setTitle("Удаление категории")
                .setMessage("Подтвердите удаление категории")
                .setAnimation(R.raw.delete_anim)
                .setPositiveButton("OK") { dialogInterface, i ->
                    val contentValues = ContentValues()
                    dbHelper = DBHelper(applicationContext, "String", 1)
                    database = dbHelper.writableDatabase
                    val cursor = database.rawQuery(
                        "SELECT * FROM " + DBHelper.Companion.TABLE_CATEGORY + " WHERE cat = ?",
                        arrayOf(autoCompleteTextView.text.toString())
                    )
                    if (cursor.moveToFirst()) {
                        id = cursor.getColumnIndex("_id")
                        id = cursor.getInt(id)
                    }
                    cursor.close()
                    database.delete(
                        DBHelper.Companion.TABLE_CATEGORY,
                        "cat = ?",
                        arrayOf(autoCompleteTextView.text.toString())
                    )
                    contentValues.clear()
                    contentValues.put(DBHelper.Companion.KEY_POSID, 1)
                    val selectionArgs = arrayOf(id.toString())
                    database.update(
                        DBHelper.Companion.TABLE_EVENTS,
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
                .setNegativeButton(view.context.resources.getString(R.string.cancel)){dialogInterface, i ->
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
            val dataIndex = c.getColumnIndex(DBHelper.Companion.KEY_DATA)
            val idIndex = c.getColumnIndex(DBHelper.Companion.KEY_ID)
            val titleIndex = c.getColumnIndex(DBHelper.Companion.KEY_TITLE)
            val descIndex = c.getColumnIndex(DBHelper.Companion.KEY_DESCRIPTIONS)
            val timebIndex = c.getColumnIndex(DBHelper.Companion.KEY_TIMEB)
            val timeeIndex = c.getColumnIndex(DBHelper.Companion.KEY_TIMEE)
            val timenotifIndex = c.getColumnIndex(DBHelper.Companion.KEY_TIMENOTIF)
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

    fun buildRV1() {
        recyclerView1 = findViewById(R.id.rv1)
        searchAdapter = SearchAdapter(this, list)
        recyclerView1.adapter = searchAdapter
    }
}