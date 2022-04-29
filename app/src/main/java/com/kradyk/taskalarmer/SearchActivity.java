package com.kradyk.taskalarmer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity);


        autoCompleteTextView = findViewById(R.id.categorysrch);
        dbHelper = new DBHelper(this, "String", 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("categories", null, null, null, null, null, null);
        cat.add("");
        if (c.moveToFirst()) {

            int catIndex = c.getColumnIndex(DBHelper.KEY_CAT);

            do {
                cat.add(c.getString(catIndex));
                Log.d("mainLog", c.getString(catIndex));
            } while (c.moveToNext());
        } else
            Log.d("mainLog", "0 rows");
        c.close();


        autoCompleteTextView.setAdapter(new SearchCatAdapter(this, R.layout.itemlayout, cat));
        listSearchView();
        buildRV1();
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listSearchView();
                buildRV1();
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
        if(!autoCompleteTextView.getText().toString().equals("")){
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
