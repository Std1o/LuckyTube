package com.akhilpatoliya.videoplayerdemo

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.database_settings
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.dbSettings
class SettingsActivity : AppCompatActivity() {
    lateinit var new_video_switcher_pref: SharedPreferences
    lateinit var switcher : Switch
    lateinit var themeSwitcher : Switch
    override fun onCreate(savedInstanceState: Bundle?) {
        dbSettings = DBSettings(this)
        database_settings = dbSettings.writableDatabase
        val cursor_theme = database_settings.query(DBSettings.TABLE_CONTACTS, null, null, null, null, null, null)
        var switcher_theme_settings_state = 0
        if (cursor_theme.moveToFirst()) {
            val themeIndex = cursor_theme.getColumnIndex(DBSettings.KEY_THEME_SWITCHER)
            switcher_theme_settings_state = cursor_theme.getInt(themeIndex)
            if (switcher_theme_settings_state == 0){
                setTheme(android.R.style.Theme_Material_NoActionBar)
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_settings)
                var layout = findViewById<LinearLayout>(R.id.activity_settings)
                layout.setBackgroundColor(Color.rgb(24,28,27))
                themeSwitcher = findViewById(R.id.switch3)
                themeSwitcher.isChecked = false;
            }
            else {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_settings)
                themeSwitcher = findViewById(R.id.switch3)
                themeSwitcher.isChecked = true;
            }
        }
        else {
            setTheme(android.R.style.Theme_Material_NoActionBar)
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_settings)
            var layout = findViewById<LinearLayout>(R.id.activity_settings)
            layout.setBackgroundColor(Color.rgb(24,28,27))
            themeSwitcher = findViewById(R.id.switch3)
            themeSwitcher.isChecked = false;
        }
        cursor_theme.close()
        switcher = findViewById(R.id.switch2)
        dbSettings = DBSettings(this)
        database_settings = dbSettings.writableDatabase
        var switcher_new_video_settings_state = 1;
        val cursor = database_settings.query(DBSettings.TABLE_CONTACTS, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val newVideoIndex = cursor.getColumnIndex(DBSettings.KEY_NEW_VIDEO_SWITCHER)
            switcher_new_video_settings_state = cursor.getInt(newVideoIndex)
            if (switcher_new_video_settings_state == 1){
                switcher.isChecked = true
            }
        }
        else {
            switcher.isChecked = true
        }
        cursor.close()


        switcher.setOnCheckedChangeListener { _, b ->
            if (switcher.isChecked){
                database_settings.delete(DBSettings.TABLE_CONTACTS, null, null)
                val contentValues = ContentValues()
                contentValues.put(DBSettings.KEY_NEW_VIDEO_SWITCHER,1)
                contentValues.put(DBSettings.KEY_THEME_SWITCHER, switcher_theme_settings_state)
                database_settings.insert(DBSettings.TABLE_CONTACTS, null, contentValues)
            }
            else {
                database_settings.delete(DBSettings.TABLE_CONTACTS, null, null)
                val contentValues = ContentValues()
                contentValues.put(DBSettings.KEY_NEW_VIDEO_SWITCHER,0)
                contentValues.put(DBSettings.KEY_THEME_SWITCHER, switcher_theme_settings_state)
                database_settings.insert(DBSettings.TABLE_CONTACTS, null, contentValues)
            }
        }

        themeSwitcher.setOnCheckedChangeListener { _, b ->
            if (themeSwitcher.isChecked){
                database_settings.delete(DBSettings.TABLE_CONTACTS, null, null)
                val contentValues = ContentValues()
                contentValues.put(DBSettings.KEY_NEW_VIDEO_SWITCHER, switcher_new_video_settings_state)
                contentValues.put(DBSettings.KEY_THEME_SWITCHER, 1)
                database_settings.insert(DBSettings.TABLE_CONTACTS, null, contentValues)
            }
            else {
                database_settings.delete(DBSettings.TABLE_CONTACTS, null, null)
                val contentValues = ContentValues()
                contentValues.put(DBSettings.KEY_NEW_VIDEO_SWITCHER, switcher_new_video_settings_state)
                contentValues.put(DBSettings.KEY_THEME_SWITCHER, 0)
                database_settings.insert(DBSettings.TABLE_CONTACTS, null, contentValues)
            }
            var mdToast : MDToast = MDToast.makeText(getApplicationContext(), "Перезапустите приложение", MDToast.LENGTH_LONG)
            mdToast.show()
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        title = "Lucky Tube - Settings"
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setTags (view : View) {
        val intent2 = Intent(this, SetSpinnerVal::class.java)
        startActivity(intent2)
    }
    fun vk (view : View) {
        val url = "https://vk.com/id327280117"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }
    fun thx (view : View) {
        val url = "https:/qiwi.com/p/79779481086"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }
    fun setCategories (view : View) {
        val intent2 = Intent(this, CategorySettings::class.java)
        startActivity(intent2)
    }
}
