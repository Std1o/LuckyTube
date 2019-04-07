package com.akhilpatoliya.videoplayerdemo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBSettings(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID

                + " integer primary key," + KEY_NEW_VIDEO_SWITCHER  + " text," + KEY_THEME_SWITCHER + " text" + ")")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("drop table if exists $TABLE_CONTACTS")

        onCreate(db)

    }

    companion object {

        val DATABASE_VERSION = 1

        val DATABASE_NAME = "settingsDb"

        val TABLE_CONTACTS = "settings"

        val KEY_ID = "_id"

        val KEY_NEW_VIDEO_SWITCHER = "newVideoSwitcher"

        val KEY_THEME_SWITCHER = "themeSwitcher"

    }

}