package com.akhilpatoliya.videoplayerdemo

import android.content.Context
import android.content.res.AssetManager

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList

class MusicTagBase(private val mContext: Context) {

    fun readLine(path: String): List<String> {
        val mLines = ArrayList<String>()

        val am = mContext.assets

        try {
            val `is` = am.open(path)
            val reader = BufferedReader(InputStreamReader(`is`))
            var line: String

            while (reader.readLine() != null)
                mLines.add(reader.readLine())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return mLines
    }
}