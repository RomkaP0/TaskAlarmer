package com.kradyk.taskalarmer

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.rm.rmswitch.RMTristateSwitch
import java.util.*


class Settings : Fragment() {


    private var dbHelper: DBHelper? = null
    private lateinit var database: SQLiteDatabase
    private val KEY_RADIOBUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX"
    private val LANG_KEY_RADIOBUTTON_INDEX = "LAND_SAVED_RADIO_BUTTON_INDEX"
    private lateinit var rmTristateSwitch: RMTristateSwitch
    private lateinit var radioGroupLang: RadioGroup
    lateinit var languagebtn: TextView
    private lateinit var signout: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewout = inflater.inflate(R.layout.settings_items, container, false)
        val wipeFills = viewout.findViewById<TextView>(R.id.wipe)
        signout = viewout.findViewById(R.id.logout)
        wipeFills.setOnClickListener {
            dbHelper = DBHelper(context, "String", 1)
            database = dbHelper!!.writableDatabase
            database.delete("fills", null, null)
        }
        rmTristateSwitch = viewout.findViewById(R.id.rmtheme)
        rmTristateSwitch.setSwitchToggleLeftDrawableRes(R.drawable.theme_auto)
        rmTristateSwitch.setSwitchToggleMiddleDrawableRes(R.drawable.theme_day)
        rmTristateSwitch.setSwitchToggleRightDrawableRes(R.drawable.theme_night)

        languagebtn = viewout.findViewById(R.id.language)
        languagebtn.setOnClickListener {

            val dialog = Dialog(requireView().context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.langlayout)
            radioGroupLang = dialog.findViewById(R.id.radiogr)
            langLoadPreferences()

            val positivebtn = dialog.findViewById<Button>(R.id.positbtn)


            positivebtn.setOnClickListener {
                if (radioGroupLang.checkedRadioButtonId == R.id.enrb){
                    langSavePreferences(LANG_KEY_RADIOBUTTON_INDEX, 0)
                    setLocale("en")}
                else{
                    langSavePreferences(LANG_KEY_RADIOBUTTON_INDEX, 1)
                    setLocale("ru")}


                dialog.hide()}

            val negativebtn = dialog.findViewById<Button>(R.id.negbtn)
            negativebtn.setOnClickListener {
                dialog.hide()
            }
            dialog.show()
            dialog.window!!.setLayout(
                (Resources.getSystem().displayMetrics.widthPixels - 20),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.window!!.setGravity(Gravity.BOTTOM)
        }


        rmTristateSwitch.addSwitchObserver { _, state ->
            when (state) {
                RMTristateSwitch.STATE_LEFT -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    savePreferences(KEY_RADIOBUTTON_INDEX, 0)
                }
                RMTristateSwitch.STATE_MIDDLE -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    savePreferences(KEY_RADIOBUTTON_INDEX, 1)
                }
                RMTristateSwitch.STATE_RIGHT -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    savePreferences(KEY_RADIOBUTTON_INDEX, 2)
                }
            }
        }





        signout.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                FirebaseAuth.getInstance().signOut()
                val st = Intent("com.example.AuthActivity")
                st.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                requireActivity().startActivityIfNeeded(st, 0)
            }
        }
        loadPreferences()
        return viewout
    }

    private fun savePreferences(key: String, value: Int) {
        val sharedPreferences = this.activity?.getSharedPreferences(
            APP_PREFERENCES, MODE_PRIVATE
        )
        val editor = sharedPreferences?.edit()
        editor?.putInt(key, value)
        editor?.apply()
    }

    private fun langSavePreferences(key: String, value: Int) {
        val sharedPreferences = this.activity?.getSharedPreferences(
            LANG_APP_PREFERENCES, MODE_PRIVATE
        )
        val editor = sharedPreferences?.edit()
        editor?.putInt(key, value)
        editor?.apply()
    }

    private fun loadPreferences() {
        val sharedPreferences = this.activity?.getSharedPreferences(
            APP_PREFERENCES, MODE_PRIVATE
        )
        val savedRadioIndex = sharedPreferences?.getInt(
            KEY_RADIOBUTTON_INDEX, 0
        )
        when (savedRadioIndex) {
            0 -> rmTristateSwitch.state = RMTristateSwitch.STATE_LEFT
            1 -> rmTristateSwitch.state = RMTristateSwitch.STATE_MIDDLE
            2 -> rmTristateSwitch.state = RMTristateSwitch.STATE_RIGHT


        }
    }

    private fun langLoadPreferences() {
        val sharedPreferences = this.activity?.getSharedPreferences(
            LANG_APP_PREFERENCES, MODE_PRIVATE
        )
        var savedRadioIndex = sharedPreferences?.getInt(
            LANG_KEY_RADIOBUTTON_INDEX, 0
        )
        if (savedRadioIndex != 0 && savedRadioIndex != 1) {
            if (Locale.getDefault().language.equals("ru"))
                savedRadioIndex = 1
            else
                savedRadioIndex = 0
        }
        val savedCheckedRadioButton =
            savedRadioIndex?.let { radioGroupLang.getChildAt(it) } as RadioButton
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

    private fun setLocale(language: String) {
        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        resources.updateConfiguration(configuration, metrics)
        onConfigurationChanged(configuration)
        requireActivity().recreate()
    }

}