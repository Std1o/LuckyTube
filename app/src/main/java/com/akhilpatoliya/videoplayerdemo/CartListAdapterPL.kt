package com.akhilpatoliya.videoplayerdemo

/**
 * Created by ravi on 26/09/17.
 */

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.Spinner
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import java.util.ArrayList

class CartListAdapterPL(private val context: Context, private val cartList: MutableList<Item>) : RecyclerView.Adapter<CartListAdapterPL.MyViewHolder1>() {
    val TYPE_FIRST_ITEM = 0
    val TYPE_ITEM = 1

    inner class MyViewHolder1(view: View) : RecyclerView.ViewHolder(view) {
        var videoTitle: TextView
        var previewImage: ImageView
        var viewBackground: RelativeLayout
        var viewForeground: RelativeLayout

        init {
            cv = itemView.findViewById<View>(R.id.cv_pl_item) as CardView
            videoTitle = itemView.findViewById<View>(R.id.video_title_pl) as TextView
            previewImage = itemView.findViewById<View>(R.id.video_preview_pl) as ImageView
            //  history = (TextView)itemView.findViewById(R.id.history);
            viewBackground = view.findViewById(R.id.view_background)
            viewForeground = view.findViewById(R.id.view_foreground)
            val playList = PlayList()
            cv.setOnClickListener {
                MDToast.makeText(cv.context, adapterPosition.toString(), MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show()
                //  cv.getContext().startActivity(new Intent(cv.getContext(), MainActivity.class)); cv/getContext may be useful
                playList.close()
                PlayList.getmInstanceActivity()!!.finish()
                MainActivity.videoTitle = PlayList.videoMyClass_PL[adapterPosition].title
                MainActivity.videoId = PlayList.videoMyClass_PL[adapterPosition].videoId
                try {//sometimes crashs on 8 element
                    CartListAdapter.space.callOnClick()
                } catch (e: NullPointerException) {
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder1 {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.play_list_item, parent, false)

        return MyViewHolder1(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder1, position: Int) {
        MainActivity.dbSettings = DBSettings(context)
        MainActivity.database_settings = MainActivity.dbSettings.writableDatabase
        val cursor = MainActivity.database_settings.query(DBSettings.TABLE_CONTACTS, null, null, null, null, null, null)
        var switcher_theme_settings_state = 0
        if (cursor.moveToFirst()) {
            val themeIndex = cursor.getColumnIndex(DBSettings.KEY_THEME_SWITCHER)
            switcher_theme_settings_state = cursor.getInt(themeIndex)
            if (switcher_theme_settings_state == 0){
                holder.viewForeground.setBackgroundColor(Color.rgb(24,28,27))
            }
            else {
                holder.viewForeground.setBackgroundColor(Color.WHITE)
            }
        }
        else {
            holder.viewForeground.setBackgroundColor(Color.rgb(24,28,27))
        }
        cursor.close()
        holder.videoTitle.text = PlayList.videoMyClass_PL[position].title
        Glide.with(context) //Takes the context
                .asBitmap()  //Tells glide that it is a bitmap
                .load("https://i.ytimg.com/vi/" + PlayList.videoMyClass_PL[position].videoId + "/hqdefault.jpg")    //Loading the image
                .apply(RequestOptions()
                        .override(375, 250)
                        .centerCrop()
                        .placeholder(R.drawable.loading))
                .into(holder.previewImage)    //into the imageview


    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    fun removeItem(position: Int) {
        cartList.removeAt(position)
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position)
        db_rewriting()
    }

    fun restoreItem(item: Item, position: Int) {
        cartList.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
        db_rewriting()
    }

    fun db_rewriting() {
        tmpList = ArrayList()
        MainActivity.database.delete(DBHelper.TABLE_CONTACTS, null, null)
        for (i in PlayList.videoMyClass_PL.indices) {
            tmpList.add(0, Item(PlayList.videoMyClass_PL[i].title, PlayList.videoMyClass_PL[i].videoId))
        }
        for (i in tmpList.indices) {
            val contentValues = ContentValues()
            contentValues.put(DBHelper.KEY_VIDEO_ID, tmpList[i].videoId)
            contentValues.put(DBHelper.KEY_TITLE, tmpList[i].title)
            contentValues.put(DBHelper.KEY_IMG, "https://i.ytimg.com/vi/" + tmpList[i].videoId + "/hqdefault.jpg")
            MainActivity.database.insert(DBHelper.TABLE_CONTACTS, null, contentValues)
        }
    }

    companion object {

        var lastTagValue: String? = null
        var lastDurValue: String? = null
        var lastEdtValue: String? = null
        lateinit var tmpList: MutableList<Item>

        lateinit var cv: CardView
        var tags: Spinner? = null
        var duration: Spinner? = null
        var gen: Button? = null
        var myTag: EditText? = null
        var space: Space? = null
        var history: TextView? = null
    }
}
