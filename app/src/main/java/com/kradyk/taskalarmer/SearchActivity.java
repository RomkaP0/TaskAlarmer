package com.kradyk.taskalarmer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    RecyclerView recyclerView1;
    SearchAdapter searchAdapter;
    ArrayList<SearchItem> list = new ArrayList();
    DBHelper dbHelper;
    AutoCompleteTextView autoCompleteTextView;
    String selection;
    String[] selectionArgs;
    ArrayList cat = new ArrayList();
    SQLiteDatabase database;
    int id=-1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.search_page);
        setContentView(R.layout.searchactivity);

        FloatingActionButton floatingActionButton1=findViewById(R.id.srchedit);
        FloatingActionButton floatingActionButton2= findViewById(R.id.srchdelete);
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                floatingActionButton1.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(251, 251, 251)));
                floatingActionButton2.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(251, 251, 251)));
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                floatingActionButton1.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(40, 40, 40)));
                floatingActionButton2.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(40, 40, 40)));
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                floatingActionButton1.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(251, 251, 251)));
                floatingActionButton2.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(251, 251, 251)));
                break;
        }
        floatingActionButton2.setImageTintList(ColorStateList.valueOf(Color.rgb(228, 68, 68)));


        autoCompleteTextView = findViewById(R.id.categorysrch);
        dbHelper = new DBHelper(this, "String", 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("categories", null, null, null, null, null, null);
        if (c.moveToFirst()) {

            int catIndex = c.getColumnIndex(DBHelper.KEY_CAT);

            do {
                cat.add(c.getString(catIndex));
                Log.d("mainLog", c.getString(catIndex));
            } while (c.moveToNext());
        } else
            Log.d("mainLog", "0 rows");
        c.close();


        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cat));
        autoCompleteTextView.setText("Without", false);
        listSearchView();
        buildRV1();
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(autoCompleteTextView.getText().toString().equals("Without")){
                    floatingActionButton1.setVisibility(View.GONE);
                    floatingActionButton2.setVisibility(View.GONE);
                }else{
                    floatingActionButton1.setVisibility(View.VISIBLE);
                    floatingActionButton2.setVisibility(View.VISIBLE);
                }
                listSearchView();
                buildRV1();
            }
        });
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputEditTextField = new EditText(view.getContext());
                inputEditTextField.setText(autoCompleteTextView.getText().toString());
                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Введите название категории")
                        .setView(inputEditTextField)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContentValues contentValues = new ContentValues();
                                dbHelper= new DBHelper(getApplicationContext(),"String",1);
                                database = dbHelper.getWritableDatabase();
                                contentValues.clear();
                                contentValues.put(DBHelper.KEY_CAT, inputEditTextField.getText().toString());
                                database.update(DBHelper.TABLE_CATEGORY, contentValues, "cat = ?", new String[]{autoCompleteTextView.getText().toString()});
                                cat.set(cat.indexOf(autoCompleteTextView.getText().toString()), inputEditTextField.getText().toString());
                                autoCompleteTextView.setText(inputEditTextField.getText().toString(), false);
                                listSearchView();
                                buildRV1();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
                dialog.show();
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Удаление категории")
                        .setMessage("Подтвердите удаление категории")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContentValues contentValues = new ContentValues();
                                dbHelper= new DBHelper(getApplicationContext(),"String",1);
                                database = dbHelper.getWritableDatabase();
                                Cursor cursor = database.rawQuery("SELECT * FROM " +DBHelper.TABLE_CATEGORY+ " WHERE cat = ?", new String[] {autoCompleteTextView.getText().toString()});
                                if(cursor.moveToFirst()) {
                                    id = cursor.getColumnIndex("_id");
                                    id = cursor.getInt(id);
                                }
                                cursor.close();
                                database.delete(DBHelper.TABLE_CATEGORY, "cat = ?", new String[]{autoCompleteTextView.getText().toString()});
                                contentValues.clear();
                                contentValues.put(DBHelper.KEY_POSID, 1);
                                String[] selectionArgs = { String.valueOf(id) };
                                database.update(DBHelper.TABLE_EVENTS, contentValues,"posid = ?", selectionArgs);
                                cat.remove(cat.indexOf(autoCompleteTextView.getText().toString()));
                                autoCompleteTextView.setText("Without", false);
                                listSearchView();
                                buildRV1();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
                dialog.show();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.appBarSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listSearchView();
            }
        });
        return true;
    }
    private void listSearchView() {
        list.clear();
        dbHelper = new DBHelper(this, "String", 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String table = "events as EV inner join categories as CT on EV.posid = CT._id";
        if(!autoCompleteTextView.getText().toString().equals("Without")){
            selection = "CT.cat = ?";
            selectionArgs = new String[]{autoCompleteTextView.getText().toString()};}
        else {selection = null;
            selectionArgs = null;}


        Cursor c = db.query(table, null, selection, selectionArgs, null, null, "title ASC");
        if (c.moveToFirst()) {

            int dataIndex = c.getColumnIndex(DBHelper.KEY_DATA);
            int idIndex = c.getColumnIndex(DBHelper.KEY_ID);

            int titleIndex = c.getColumnIndex(DBHelper.KEY_TITLE);
            int descIndex = c.getColumnIndex(DBHelper.KEY_DESCRIPTIONS);

            int timebIndex = c.getColumnIndex(DBHelper.KEY_TIMEB);
            int timeeIndex = c.getColumnIndex(DBHelper.KEY_TIMEE);
            int timenotifIndex = c.getColumnIndex(DBHelper.KEY_TIMENOTIF);

            do {

                list.add(new SearchItem(c.getString(idIndex), c.getString(titleIndex), c.getString(dataIndex), c.getString(timebIndex), c.getString(timeeIndex), c.getString(timenotifIndex), c.getString(descIndex)));
                Log.d("mainLog", c.getString(idIndex));
            } while (c.moveToNext());
        } else
            Log.d("mainLog", "0 rows");
        c.close();
        buildRV1();
    }
    public void buildRV1() {
        recyclerView1 = findViewById(R.id.rv1);
        searchAdapter = new SearchAdapter(this, list);
        recyclerView1.setAdapter(searchAdapter);

    }
}
