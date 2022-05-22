@file:Suppress("PropertyName")

package com.kradyk.taskalarmer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var chipNavigationBar: ChipNavigationBar
    override fun onCreate(savedInstanceState: Bundle?) {
        langLoadPreferences()
        println("CREATE")
        loadPreferences()

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            val st = Intent("com.example.AuthActivity")
            st.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivityIfNeeded(st, 0)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chipNavigationBar = findViewById(R.id.navbar)
        chipNavigationBar.setItemSelected(R.id.button1, true)
        if (c == 0)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment()).commit()
        else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentcur!!).commit()
        c=0}
            chipNavigationBar.setOnItemSelectedListener {
                when (it) {
                    R.id.button1 -> fragmentcur = MainFragment()
                    R.id.button2 -> fragmentcur = SearchActivity()
                    R.id.button3 -> fragmentcur = Settings()
                }
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    fragmentcur!!
                ).commit()
            }


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

    override fun recreate() {
        super.recreate()
        c+=1
        println("Recreate")
    }
    companion object{
        var c: Int = 0
        var fragmentcur: Fragment? = null

    }
}