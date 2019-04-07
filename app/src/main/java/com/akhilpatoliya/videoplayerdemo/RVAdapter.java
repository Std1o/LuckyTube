package com.akhilpatoliya.videoplayerdemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public final int TYPE_FIRST_ITEM = 0;
    public final int TYPE_ITEM = 1;

    public static String lastTagValue;
    public static String lastDurValue;
    public static String lastEdtValue;

    public static TextView videoTitle;
    public static ImageView previewImage;
    public static CardView cv;
    ArrayAdapter<String> dataAdapter;
    public static Spinner tags;
    public static String db_tag;
    public static Spinner duration;
    public static ImageButton gen;
    public static Space space;
    public static TextView history;

    public class EmptyHistory extends RecyclerView.ViewHolder{

        public EmptyHistory(View itemView) {
            super(itemView);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        VideoViewHolder(final View itemView) {
            super(itemView);
            itemView.setTag(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            videoTitle = (TextView)itemView.findViewById(R.id.video_title);
            previewImage = (ImageView)itemView.findViewById(R.id.video_preview_pl);
        }
    }

    public class BigViewHolder extends RecyclerView.ViewHolder {

        BigViewHolder(View itemView) {
            super(itemView);


            dataAdapter = new ArrayAdapter<String>(itemView.getContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tags.setAdapter(dataAdapter);
            Cursor cursor = MainActivity.Companion.getDatabase2().query(DBTags.Companion.getTABLE_CONTACTS(), null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int tagIndex = cursor.getColumnIndex(DBTags.Companion.getKEY_TAG());
                do {
                    db_tag = cursor.getString(tagIndex);
                    dataAdapter.add(db_tag);
                    dataAdapter.notifyDataSetChanged();
                } while (cursor.moveToNext());
            }
            if (dataAdapter.isEmpty()){
                MainActivity.Companion.getDatabase2().delete(DBTags.Companion.getTABLE_CONTACTS(), null, null);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBTags.Companion.getKEY_TAG(), "Без тега");
                MainActivity.Companion.getDatabase2().insert(DBTags.Companion.getTABLE_CONTACTS(), null, contentValues);
                contentValues.put(DBTags.Companion.getKEY_TAG(), "Музыка");
                MainActivity.Companion.getDatabase2().insert(DBTags.Companion.getTABLE_CONTACTS(), null, contentValues);
                contentValues.put(DBTags.Companion.getKEY_TAG(), "Рандомный тег");
                MainActivity.Companion.getDatabase2().insert(DBTags.Companion.getTABLE_CONTACTS(), null, contentValues);
                contentValues.put(DBTags.Companion.getKEY_TAG(), "Наука");
                MainActivity.Companion.getDatabase2().insert(DBTags.Companion.getTABLE_CONTACTS(), null, contentValues);
                dataAdapter.add("Без тега");
                dataAdapter.add("Музыка");
                dataAdapter.add("Рандомный тег");
                dataAdapter.add("Наука");
            }
            cursor.close();
            tags.setSelection(getIndex(tags, lastTagValue));
            duration.setSelection(getIndex(duration, lastDurValue));
            space = (Space) itemView.findViewById(R.id.space);

        }
    }

    public static int getIndex(Spinner spinner, String myString){
        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_FIRST_ITEM) {
            View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new BigViewHolder(v1);
        }
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new VideoViewHolder(v2);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        if(holder.getItemViewType() == TYPE_FIRST_ITEM){
            return;
        }
        try{
            videoTitle.setText(MainActivity.Companion.getCartList().get(position).title);
          //  previewImage.setImageBitmap(MainActivity.cartList.get(position).preview);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.Companion.setVideoId(MainActivity.Companion.getCartList().get(pos).videoId);
                    MainActivity.Companion.setVideoTitle(MainActivity.Companion.getCartList().get(position).title);
             //       MainActivity.preview = MainActivity.cartList.get(position).preview;

                    Item item = MainActivity.Companion.getCartList().get(pos);
                    MainActivity.Companion.getCartList().remove(pos);
                    MainActivity.Companion.getCartList().add(1, item);
                    spaceclk();
                }
            });
        }
        catch (Exception e) {}
    }
    public static void spaceclk (){
        space.callOnClick();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FIRST_ITEM;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return MainActivity.Companion.getCartList().size();
    }
}
