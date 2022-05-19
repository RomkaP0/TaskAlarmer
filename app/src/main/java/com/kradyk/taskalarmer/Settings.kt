package com.kradyk.taskalarmer

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth


class Settings : AppCompatActivity() {


    private var dbHelper: DBHelper? = null
    private lateinit var database: SQLiteDatabase
    private val KEY_RADIOBUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX"
    private lateinit var radioGroupTheme: RadioGroup
    private val LANG_KEY_RADIOBUTTON_INDEX = "LAND_SAVED_RADIO_BUTTON_INDEX"
    private lateinit var radioGroupLang: RadioGroup
    private lateinit var signout: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_items)
        val wipeFills = findViewById<Button>(R.id.wipeFills)
        signout = findViewById(R.id.signout)
        wipeFills.setOnClickListener {
            dbHelper = DBHelper(baseContext, "String", 1)
            database = dbHelper!!.writableDatabase
            database.delete("fills", null, null)
        }
        radioGroupTheme = findViewById(R.id.themeRG)
        radioGroupTheme.setOnCheckedChangeListener { _, i ->
            when (i) {
                -1 -> {}
                R.id.systemThemeRB -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    savePreferences(KEY_RADIOBUTTON_INDEX, 0)
                }
                R.id.lightThemeRB -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    savePreferences(KEY_RADIOBUTTON_INDEX, 1)
                }
                R.id.darkThemeRB -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    savePreferences(KEY_RADIOBUTTON_INDEX, 2)
                }
                else -> {}
            }
        }
        radioGroupLang = findViewById(R.id.langRG)
        radioGroupLang.setOnCheckedChangeListener { _, i ->
            when (i) {
                -1 -> {}
                R.id.EN -> {
                    langSavePreferences(LANG_KEY_RADIOBUTTON_INDEX, 0)
                    c += 1
                    if (c > 1) finish()
                }
                R.id.RU -> {
                    langSavePreferences(LANG_KEY_RADIOBUTTON_INDEX, 1)
                    c += 1
                    if (c > 1) finish()
                }
                else -> {}
            }
        }
        val materialToolbar = findViewById<MaterialToolbar>(R.id.topAppBarSt)
        materialToolbar.setTitle(R.string.setting_page)
        materialToolbar.setNavigationOnClickListener { finish() }
        signout.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                FirebaseAuth.getInstance().signOut()
            }
        }
        langLoadPreferences()
        loadPreferences()
    }

    private fun savePreferences(key: String, value: Int) {
        val sharedPreferences = getSharedPreferences(
            APP_PREFERENCES, MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun langSavePreferences(key: String, value: Int) {
        val sharedPreferences = getSharedPreferences(
            LANG_APP_PREFERENCES, MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun loadPreferences() {
        val sharedPreferences = getSharedPreferences(
            APP_PREFERENCES, MODE_PRIVATE
        )
        val savedRadioIndex = sharedPreferences.getInt(
            KEY_RADIOBUTTON_INDEX, 0
        )
        val savedCheckedRadioButton = radioGroupTheme
            .getChildAt(savedRadioIndex) as RadioButton
        savedCheckedRadioButton.isChecked = true
    }

    private fun langLoadPreferences() {
        val sharedPreferences = getSharedPreferences(
            LANG_APP_PREFERENCES, MODE_PRIVATE
        )
        val savedRadioIndex = sharedPreferences.getInt(
            LANG_KEY_RADIOBUTTON_INDEX, 0
        )
        val savedCheckedRadioButton = radioGroupLang.getChildAt(savedRadioIndex) as RadioButton
        savedCheckedRadioButton.isChecked = true
    }

    override fun onStop() {
        c = 0
        super.onStop()
    }

    companion object {
        const val APP_PREFERENCES = "theme"
        var c = 0 //DON`T TOUCH THIS
        const val LANG_APP_PREFERENCES = "lang"
    }

}