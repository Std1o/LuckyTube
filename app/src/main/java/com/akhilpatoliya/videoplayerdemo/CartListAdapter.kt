package com.akhilpatoliya.videoplayerdemo

/**
 * Created by ravi on 26/09/17.
 */

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.database_settings
import com.akhilpatoliya.videoplayerdemo.MainActivity.Companion.dbSettings

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import java.util.ArrayList

class CartListAdapter(private val context: Context, private val cartList: MutableList<Item>) : RecyclerView.Adapter<CartListAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var videoTitle: TextView
        var previewImage: ImageView
        var viewBackground: RelativeLayout
        var viewForeground: RelativeLayout

        init {
            videoTitle = itemView.findViewById<View>(R.id.video_title_pl) as TextView
            previewImage = itemView.findViewById<View>(R.id.video_preview_pl) as ImageView
            //  history = (TextView)itemView.findViewById(R.id.history);
            cv = itemView.findViewById<View>(R.id.cv) as CardView
            space = itemView.findViewById<View>(R.id.space) as Space
            viewBackground = view.findViewById(R.id.view_background)
            viewForeground = view.findViewById(R.id.view_foreground)
            cv.setOnClickListener {
                MainActivity.videoId = MainActivity.cartList[position].videoId
                MainActivity.videoTitle = MainActivity.cartList[position].title
                space.callOnClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
        holder.videoTitle.text = MainActivity.cartList[position].title
        Glide.with(context) //Takes the context
                .asBitmap()  //Tells glide that it is a bitmap
                .load("https://i.ytimg.com/vi/" + MainActivity.cartList[position].videoId + "/hqdefault.jpg")    //Loading the image
                .apply(RequestOptions()
                        .override(375, 250)
                        .centerCrop()
                        .placeholder(R.drawable.loading))
                .into(holder.previewImage)    //into the imageview
        if (position == MainActivity.cartList.size - 4) {
            Machine.something()
        }
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
    }

    fun restoreItem(item: Item, position: Int) {
        cartList.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }

    companion object {

        lateinit var cv: CardView
        var tags: Spinner? = null
        var duration: Spinner? = null
        var db_tag: String? = null
        var gen: Button? = null
        var myTag: EditText? = null
        lateinit var space: Space
        var history: TextView? = null
    }
}
