package com.akhilpatoliya.videoplayerdemo

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.database_category
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.database_settings
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.dbCategories
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.dbSettings
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.mCategories

class CategorySettings : AppCompatActivity() {

    companion object {
        val APP_PREFERENCES = "include_tags_switcher_state"
        val APP_PREFERENCES_KEY = "include_tags_switcher_state_key"
        lateinit var include_tags_switcher_pref: SharedPreferences
    }

    lateinit var switcher : Switch
    lateinit var tags_settings : Button
    lateinit var divider_button : View

    lateinit var Films_and_animations: CheckBox
    lateinit var Autos_and_Vehicles: CheckBox
    lateinit var animals: CheckBox
    lateinit var sport: CheckBox
    lateinit var travels: CheckBox
    lateinit var games: CheckBox
    lateinit var people_and_blogs: CheckBox
    lateinit var comedy: CheckBox
    lateinit var Entertainment: CheckBox
    lateinit var News_and_Politics: CheckBox
    lateinit var Howto_style: CheckBox
    lateinit var Education: CheckBox
    lateinit var Science_and_Technology: CheckBox
    lateinit var Nonprofits_and_Activism: CheckBox


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
                setContentView(R.layout.activity_category_settings)
                findViewById<LinearLayout>(R.id.category_settings).setBackgroundColor(Color.rgb(24,28,27))
            }
            else {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_category_settings)
            }
        }
        else {
            setTheme(android.R.style.Theme_Material_NoActionBar)
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_category_settings)
            findViewById<LinearLayout>(R.id.category_settings).setBackgroundColor(Color.rgb(24,28,27))
        }
        cursor_theme.close()
        include_tags_switcher_pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        title = "Lucky Tube - Category Settings"
        toolbar.setNavigationOnClickListener {
            MDToast.makeText(this, "Настройки сохранены", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show()
            onBackPressed()
        }

        switcher = findViewById(R.id.switch1)
        tags_settings = findViewById(R.id.set_tags)
        divider_button = findViewById(R.id.divider_button)

        var switcher_include_tags_settings_state = include_tags_switcher_pref.getInt(APP_PREFERENCES_KEY, 0)
        if (switcher_include_tags_settings_state == 1) {
            switcher.isChecked = true
            tags_settings.visibility = VISIBLE
            divider_button.visibility = VISIBLE
        }

        switcher.setOnCheckedChangeListener { _, b ->
            if (switcher.isChecked){
                tags_settings.visibility = VISIBLE
                divider_button.visibility = VISIBLE
                val editor = include_tags_switcher_pref.edit()
                editor.putInt(APP_PREFERENCES_KEY, 1)
                editor.apply()
            }
            else {
                tags_settings.visibility = GONE
                divider_button.visibility = GONE
                val editor = include_tags_switcher_pref.edit()
                editor.putInt(APP_PREFERENCES_KEY, 0)
                editor.apply()
            }
        }

        dbCategories = DBCategories(this)
        database_category = dbCategories.writableDatabase

        Films_and_animations = findViewById(R.id.Films_and_animations)
        Autos_and_Vehicles = findViewById(R.id.Autos_and_Vehicles)
        animals = findViewById(R.id.animals)
        sport = findViewById(R.id.sport)
        travels = findViewById(R.id.travels)
        games = findViewById(R.id.games)
        people_and_blogs = findViewById(R.id.people_and_blogs)
        comedy = findViewById(R.id.comedy)
        Entertainment = findViewById(R.id.Entertainment)
        News_and_Politics = findViewById(R.id.News_and_Politics)
        Howto_style = findViewById(R.id.Howto_style)
        Education = findViewById(R.id.Education)
        Science_and_Technology = findViewById(R.id.Science_and_Technology)
        Nonprofits_and_Activism = findViewById(R.id.Nonprofits_and_Activism)


        mCategories.clear()
        val cursor = database_category.query(DBCategories.TABLE_CONTACTS, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val tagIndex = cursor.getColumnIndex(DBCategories.KEY_CATEGORY)
            do {
                mCategories.add(cursor.getString(tagIndex))
            } while (cursor.moveToNext())
        }
        cursor.close()

        if (mCategories.contains("1")) {
            Films_and_animations.isChecked = true
        }
        if (mCategories.contains("2")) {
            Autos_and_Vehicles.isChecked = true
        }
        if (mCategories.contains("15")) {
            animals.isChecked = true
        }
        if (mCategories.contains("17")) {
            sport.isChecked = true
        }
        if (mCategories.contains("19")) {
            travels.isChecked = true
        }
        if (mCategories.contains("20")) {
            games.isChecked = true
        }
        if (mCategories.contains("22")) {
            people_and_blogs.isChecked = true
        }
        if (mCategories.contains("23")) {
            comedy.isChecked = true
        }
        if (mCategories.contains("24")) {
            Entertainment.isChecked = true
        }
        if (mCategories.contains("25")) {
            News_and_Politics.isChecked = true
        }
        if (mCategories.contains("26")) {
            Howto_style.isChecked = true
        }
        if (mCategories.contains("27")) {
            Education.isChecked = true
        }
        if (mCategories.contains("28")) {
            Science_and_Technology.isChecked = true
        }
        if (mCategories.contains("29")) {
            Nonprofits_and_Activism.isChecked = true
        }



        Films_and_animations.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("1")

                db_rewriting()
            } else {
                mCategories.remove("1")

                db_rewriting()
            }
        }
        Autos_and_Vehicles.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("2")

                db_rewriting()
            } else {
                mCategories.remove("2")

                db_rewriting()
            }
        }
        animals.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("15")

                db_rewriting()
            } else {
                mCategories.remove("15")

                db_rewriting()
            }
        }
        sport.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("17")

                db_rewriting()
            } else {
                mCategories.remove("17")

                db_rewriting()
            }
        }
        travels.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("19")

                db_rewriting()
            } else {
                mCategories.remove("19")

                db_rewriting()
            }
        }
        games.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("20")

                db_rewriting()
            } else {
                mCategories.remove("20")

                db_rewriting()
            }
        }
        people_and_blogs.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("22")

                db_rewriting()
            } else {
                mCategories.remove("22")

                db_rewriting()
            }
        }
        comedy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("23")

                db_rewriting()
            } else {
                mCategories.remove("23")

                db_rewriting()
            }
        }
        Entertainment.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("24")

                db_rewriting()
            } else {
                mCategories.remove("24")

                db_rewriting()
            }
        }
        News_and_Politics.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("25")

                db_rewriting()
            } else {
                mCategories.remove("25")

                db_rewriting()
            }
        }
        Howto_style.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("26")

                db_rewriting()
            } else {
                mCategories.remove("26")

                db_rewriting()
            }
        }
        Education.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("27")

                db_rewriting()
            } else {
                mCategories.remove("27")

                db_rewriting()
            }
        }
        Science_and_Technology.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("28")

                db_rewriting()
            } else {
                mCategories.remove("28")

                db_rewriting()
            }
        }
        Nonprofits_and_Activism.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCategories.add("29")

                db_rewriting()
            } else {
                mCategories.remove("29")

                db_rewriting()
            }
        }
    }

    fun db_rewriting() {
        database_category.delete(DBCategories.TABLE_CONTACTS, null, null)
        for (i in mCategories.indices) {
            val contentValues = ContentValues()
            contentValues.put(DBCategories.KEY_CATEGORY, mCategories.get(i))
            database_category.insert(DBCategories.TABLE_CONTACTS, null, contentValues)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.repeatCount == 0) {
            MDToast.makeText(this, "Настройки сохранены", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show()
            onBackPressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun groupTagsSet(view : View ){
        val intent2 = Intent(this, TagListActivity::class.java)
        startActivity(intent2)
    }
}
