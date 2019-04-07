package com.akhilpatoliya.videoplayerdemo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.akhilpatoliya.videoplayerdemo.MainActivity.database_settings;

public class TagListActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    SwipeToDeleteListenerCallbackHelper cbh;
    static EditText editTag;
    public static String db_tag;
    public static  List<String> mDataSet = new ArrayList<String>();
    public static Context mcontext;
    public static final MyAdapter mAdapter = new MyAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Cursor cursor_theme = database_settings.query(DBSettings.Companion.getTABLE_CONTACTS(), null, null, null, null, null, null);
        int switcher_theme_settings_state = 0;
        if (cursor_theme.moveToFirst()) {
            int themeIndex = cursor_theme.getColumnIndex(DBSettings.Companion.getKEY_THEME_SWITCHER());
            switcher_theme_settings_state = cursor_theme.getInt(themeIndex);
            if (switcher_theme_settings_state == 0){
                setTheme(android.R.style.Theme_Material_NoActionBar);
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_tag_list2);
                findViewById(R.id.tag_list).setBackgroundColor(Color.rgb(24,28,27));
            }
            else {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_tag_list);
            }
        }
        else {
            setTheme(android.R.style.Theme_Material_NoActionBar);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tag_list2);
            findViewById(R.id.tag_list).setBackgroundColor(Color.rgb(24,28,27));
        }
        cursor_theme.close();
        mcontext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDToast.Companion.makeText(mcontext, "Настройки сохранены", MDToast.Companion.getLENGTH_SHORT(), MDToast.Companion.getTYPE_SUCCESS()).show();
                onBackPressed();
            }
        });
        setTitle("Lucky Tube - tag's setting");

        final ListView lv = (ListView) findViewById(R.id.listView);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        Cursor cursor = MainActivity.Companion.getDatabase_group_search_tags().query(DBGroupSearchTags.Companion.getTABLE_CONTACTS(), null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int tagIndex = cursor.getColumnIndex(DBGroupSearchTags.Companion.getKEY_GROUP_SEARCH_TAG());
            do {
                db_tag = cursor.getString(tagIndex);
                mDataSet.add(db_tag);
                mAdapter.notifyDataSetChanged();
            } while (cursor.moveToNext());
        }
        editTag = findViewById(R.id.editTag);
        editTag.setOnEditorActionListener(this);
        FloatingActionButton clear =  findViewById(R.id.clear_tags);
        ImageButton add =  findViewById(R.id.addTag);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear_tags();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.add_tag();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                editTag.setText("");
            }
        });
        lv.setAdapter(mAdapter);

        cbh = new SwipeToDeleteListenerCallbackHelper();

        SwipeToDeleteListener swipeToDeleteListener = new SwipeToDeleteListener(lv, new SwipeToDeleteListener.SwipeCallbacks() {
            @Override
            public void onSwipe(int position) {
                //todo
            }

            @Override
            public void onOpen(View view, final int position) {
                TextView deleteText = (TextView) view.findViewById(R.id.txt_delete);
                deleteText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(TagListActivity.this, position);
                    }
                });

                TextView cancelText = (TextView) view.findViewById(R.id.txt_cancel);
                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cbh.getSwipeToDeleteListener().cancelPendingRow(null, 0, false);
                    }
                });

            }

            @Override
            public void onClose(View view, int position) {
                //todo
            }

            @Override
            public void onDelete(View view, int position) {
                mAdapter.remove(position);
            }
        });

        cbh.setSwipeToDeleteListener(swipeToDeleteListener);

        lv.setOnTouchListener(swipeToDeleteListener);
        swipeToDeleteListener.makeScrollListener();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            MDToast.Companion.makeText(mcontext, "Настройки сохранены", MDToast.Companion.getLENGTH_SHORT(), MDToast.Companion.getTYPE_SUCCESS()).show();
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            mAdapter.add_tag();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            editTag.setText("");
            return true;
        }
        return false;
    }


    public void deleteItem(Context context, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

//        // set title
//        alertDialogBuilder.setTitle("Delete item");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        cbh.getSwipeToDeleteListener().deletePendingRow(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        cbh.getSwipeToDeleteListener().cancelPendingRow(null, 0, false);
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.setCancelable(true);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    static class MyAdapter extends BaseAdapter {
        private static final int SIZE = 100;


        MyAdapter() {

        }

        @Override
        public int getCount() {
            return mDataSet.size();
        }

        @Override
        public String getItem(int position) {
            return mDataSet.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int position) {
            mDataSet.remove(position);
            notifyDataSetChanged();
            db_rewriting();
        }
        public void clear_tags() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    mcontext);

//        // set title
//        alertDialogBuilder.setTitle("Delete item");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Do you want to delete this item?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            MainActivity.Companion.getDatabase_group_search_tags().delete(DBGroupSearchTags.Companion.getTABLE_CONTACTS(), null, null);
                            mDataSet.clear();
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            alertDialogBuilder.setCancelable(true);

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        public void add_tag() {
            mDataSet.add(editTag.getText().toString());
            notifyDataSetChanged();
            db_rewriting();
        }

        static class ViewHolder {
            TextView dataTextView;

            ViewHolder(View view) {
                dataTextView = ((TextView) view.findViewById(R.id.txt_data));
                view.setTag(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false))
                    : (ViewHolder) convertView.getTag();

            viewHolder.dataTextView.setText(mDataSet.get(position));
            Cursor cursor_theme = database_settings.query(DBSettings.Companion.getTABLE_CONTACTS(), null, null, null, null, null, null);
            int switcher_theme_settings_state = 0;
            if (cursor_theme.moveToFirst()) {
                int themeIndex = cursor_theme.getColumnIndex(DBSettings.Companion.getKEY_THEME_SWITCHER());
                switcher_theme_settings_state = cursor_theme.getInt(themeIndex);
                if (switcher_theme_settings_state == 0){
                    viewHolder.dataTextView.setTextColor(Color.rgb(253,253,253));
                    viewHolder.dataTextView.setBackgroundColor(Color.rgb(24,28,27));
                }
            }
            else {
                viewHolder.dataTextView.setTextColor(Color.rgb(253,253,253));
                viewHolder.dataTextView.setBackgroundColor(Color.rgb(24,28,27));
            }
            return convertView;
        }
    }

    public static void db_rewriting() {
        MainActivity.Companion.getDatabase_group_search_tags().delete(DBGroupSearchTags.Companion.getTABLE_CONTACTS(), null, null);
        for (int i = 0; i < mDataSet.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBGroupSearchTags.Companion.getKEY_GROUP_SEARCH_TAG(), mDataSet.get(i));
            MainActivity.Companion.getDatabase_group_search_tags().insert(DBGroupSearchTags.Companion.getTABLE_CONTACTS(), null, contentValues);
        }
    }


    public class SwipeToDeleteListenerCallbackHelper {
        private SwipeToDeleteListener swipeToDeleteListener;

        public void setSwipeToDeleteListener(SwipeToDeleteListener swipeToDeleteListener) {
            this.swipeToDeleteListener = swipeToDeleteListener;
        }

        public SwipeToDeleteListener getSwipeToDeleteListener() {
            return swipeToDeleteListener;
        }
    }
    /* use this class to setup swipetodelete listener */
}
