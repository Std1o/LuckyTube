package com.akhilpatoliya.videoplayerdemo

import android.app.AlertDialog
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.database_settings
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.dbSettings
import com.google.api.services.youtube.YouTube

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

class PlayList : AppCompatActivity(), RecyclerItemTouchHelperPL.RecyclerItemTouchHelperListener {
    internal lateinit var textToSend: String
    private var coordinatorLayout: CoordinatorLayout? = null

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
                setContentView(R.layout.play_list2)
                findViewById<CoordinatorLayout>(R.id.cv_pl).setBackgroundColor(Color.rgb(24,28,27))
            }
            else {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.play_list)
            }
        }
        else {
            setTheme(android.R.style.Theme_Material_NoActionBar)
            super.onCreate(savedInstanceState)
            setContentView(R.layout.play_list2)
            findViewById<CoordinatorLayout>(R.id.cv_pl).setBackgroundColor(Color.rgb(24,28,27))
        }
        cursor_theme.close()
        mInstanceActivity = this
        rv = findViewById<View>(R.id.rv_pl) as RecyclerView

        coordinatorLayout = findViewById(R.id.cv_pl)
        videoMyClass_PL = ArrayList()
        mAdapter = CartListAdapterPL(this, videoMyClass_PL)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        rv.layoutManager = mLayoutManager
        rv.itemAnimator = DefaultItemAnimator()
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv.adapter = mAdapter

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        val itemTouchHelperCallback = RecyclerItemTouchHelperPL(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv)


        // making http call and fetching menu json

        val itemTouchHelperCallback1 = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(rv)
        val cursor = MainActivity.database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null)


        if (cursor.moveToFirst()) {
            val vide0IDIndex = cursor.getColumnIndex(DBHelper.KEY_VIDEO_ID)
            val titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE)
            val imgIndex = cursor.getColumnIndex(DBHelper.KEY_IMG)
            do {
                videoTitle = cursor.getString(titleIndex)
                videoId = cursor.getString(vide0IDIndex)
                tmpPreview = cursor.getString(imgIndex)
                videoMyClass_PL.add(0, Item(videoTitle, videoId))

                mAdapter!!.notifyDataSetChanged()
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is CartListAdapterPL.MyViewHolder1) {
            // get the removed item name to display it in snack bar

            // backup of removed item for undo purpose
            val deletedItem = videoMyClass_PL[viewHolder.getAdapterPosition()]
            val deletedIndex = viewHolder.getAdapterPosition()

            // remove the item from recycler view
            mAdapter!!.removeItem(viewHolder.getAdapterPosition())

            // showing snack bar with Undo option
            val snackbar = Snackbar
                    .make(coordinatorLayout!!, " removed from cart!", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") {
                // undo is selected, restore the deleted item
                mAdapter!!.restoreItem(deletedItem, deletedIndex)
            }
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
    }

    fun clear_play_list(v: View) {
        val alertDialogBuilder = AlertDialog.Builder(
                this)

        //        // set title
        //        alertDialogBuilder.setTitle("Delete item");

        // set dialog message
        alertDialogBuilder
                .setMessage("Очистить плейлист?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    // if this button is clicked, close
                    // current activity
                    MainActivity.database.delete(DBHelper.TABLE_CONTACTS, null, null)
                    videoMyClass_PL.clear()
                    mAdapter!!.notifyDataSetChanged()
                }
                .setNegativeButton("No") { dialog, id ->
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel()
                }

        alertDialogBuilder.setCancelable(true)

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mInstanceActivity = null
    }


    fun share(v: View) {
        textToSend = ""
        for (i in PlayList.videoMyClass_PL.indices) {
            textToSend = textToSend + "https://www.youtube.com/watch?v=" + PlayList.videoMyClass_PL[i].videoId + "\n"
        }
        var mdToast : MDToast = MDToast.makeText(getApplicationContext(), "Ссылки скопированы в буфер обмена", MDToast.LENGTH_LONG)
        var clipboard : ClipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clip : ClipData = ClipData.newPlainText("", textToSend)
        mdToast.show()
        clipboard.primaryClip = clip

    }

    fun close() {

    }

    companion object {
        lateinit var rv: RecyclerView
        lateinit var videoMyClass_PL: MutableList<Item>
        lateinit var videoTitle: String
        var preview: Bitmap? = null
        private var mAdapter: CartListAdapterPL? = null
        lateinit var videoId: String
        lateinit var tmpPreview: String
        var url: URL? = null
        private var mInstanceActivity: PlayList? = null


        fun getmInstanceActivity(): PlayList? {
            return mInstanceActivity
        }
    }
}
