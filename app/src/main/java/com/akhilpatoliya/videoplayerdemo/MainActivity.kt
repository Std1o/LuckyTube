package com.akhilpatoliya.videoplayerdemo

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.arch.lifecycle.Lifecycle
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.*
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import com.akhilpatoliya.videoplayerdemo.CategorySettings.Companion.APP_PREFERENCES_KEY
import com.akhilpatoliya.videoplayerdemo.CategorySettings.Companion.include_tags_switcher_pref

import com.akhilpatoliya.videoplayerdemo.utils.FullScreenHelper
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bvapp.arcmenulibrary.ArcMenu
import com.google.api.client.util.DateTime
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerFullScreenListener
import com.skyreds.ytextractor.VideoMeta
import com.skyreds.ytextractor.YouTubeExtractor
import com.skyreds.ytextractor.YtFile
import org.jetbrains.anko.doAsync

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap
import java.util.Random

class MainActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    lateinit var context: Context
    lateinit var rv: RecyclerView
    var musicTagsList: ArrayList<String> = ArrayList()
    lateinit var pref: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    lateinit var builder: AlertDialog.Builder
    private var coordinatorLayout: CoordinatorLayout? = null
    private val IDD_LIST_FORMATS = 1
    private val LIST_TAGS = 2
    var fullScreenHelper = FullScreenHelper(this)

    internal lateinit var progressDialog: ProgressDialog
    internal lateinit var downloadManager: DownloadManager
    internal var title_downloaded_video = ""
    internal var ext = ""

    val tag: Pair<String, String?>
        get() {
            val stringTag: String
            val category: String?
            when (last_taag_value) {

                "Без тега" -> {
                    val r = Random()
                    val seq = "abcdefghijklmnopqrstuvwxyz0123456789-_"
                    var randSeq = ""
                    val seq1 = "абвгдеёжзийклмнопрстуфхцчшщъыьэю"
                    val randSeq1 = seq1[r.nextInt(seq1.length)]
                    val char_count = 3 + (Math.random() * 2).toInt()
                    println("MainActivity: $char_count")
                    for (i in 0 until char_count) {
                        randSeq += seq[r.nextInt(seq.length)]
                    }
                    stringTag = "$randSeq а|б|в|г|д|е|ё|ж|з|и|й|к|л|м|н|о|п|р|с|т|у|ф|х|ц|ч|ш|щ|ъ|ы|ь|э|ю"
                    category = null
                }
                "Наука и техника" -> {
                    val r = Random()
                    val seq = "abcdefghijklmnopqrstuvwxyz0123456789-_"
                    var randSeq = ""
                    val seq1 = "абвгдеёжзийклмнопрстуфхцчшщъыьэю"
                    val randSeq1 = seq1[r.nextInt(seq1.length)]
                    val char_count = 3 + (Math.random() * 2).toInt()
                    println("MainActivity: $char_count")
                    for (i in 0 until char_count) {
                        randSeq += seq[r.nextInt(seq.length)]
                    }
                    stringTag = "$randSeq а|б|в|г|д|е|ё|ж|з|и|й|к|л|м|н|о|п|р|с|т|у|ф|х|ц|ч|ш|щ|ъ|ы|ь|э|ю"
                    category = "28"
                }

                "Юмор" -> {
                    val r = Random()
                    val seq = "abcdefghijklmnopqrstuvwxyz0123456789-_"
                    var randSeq = ""
                    val seq1 = "абвгдеёжзийклмнопрстуфхцчшщъыьэю"
                    val randSeq1 = seq1[r.nextInt(seq1.length)]
                    val char_count = 3 + (Math.random() * 2).toInt()
                    println("MainActivity: $char_count")
                    for (i in 0 until char_count) {
                        randSeq += seq[r.nextInt(seq.length)]
                    }
                    stringTag = "$randSeq а|б|в|г|д|е|ё|ж|з|и|й|к|л|м|н|о|п|р|с|т|у|ф|х|ц|ч|ш|щ|ъ|ы|ь|э|ю"
                    category = "23"
                }

                "Поиск по категориям" -> {
                    mCategories.clear()
                    val cursor = database_category.query(DBCategories.TABLE_CONTACTS, null, null, null, null, null, null)
                    if (cursor.moveToFirst()) {
                        val tagIndex = cursor.getColumnIndex(DBCategories.KEY_CATEGORY)
                        do {
                            mCategories.add(cursor.getString(tagIndex))
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                    mgroupSearchTags.clear()
                    val cursormgroupSearchTags = database_group_search_tags.query(DBGroupSearchTags.TABLE_CONTACTS, null, null, null, null, null, null)
                    if (cursormgroupSearchTags.moveToFirst()) {
                        val tagIndex = cursormgroupSearchTags.getColumnIndex(DBGroupSearchTags.KEY_GROUP_SEARCH_TAG)
                        do {
                            mgroupSearchTags.add(cursormgroupSearchTags.getString(tagIndex))
                        } while (cursormgroupSearchTags.moveToNext())
                    }
                    cursormgroupSearchTags.close()
                    val r = Random()
                    var k = r.nextInt(2)
                    include_tags_switcher_pref = getSharedPreferences(CategorySettings.APP_PREFERENCES, MODE_PRIVATE)
                    var switcher_include_tags_settings_state = include_tags_switcher_pref.getInt(APP_PREFERENCES_KEY, 0);
                    if (k > 0 || mgroupSearchTags.size == 0 || switcher_include_tags_settings_state != 1) {
                        val seq = "abcdefghijklmnopqrstuvwxyz0123456789-_"
                        var randSeq = ""
                        val char_count = 3 + (Math.random() * 2).toInt()
                        for (i in 0 until char_count) {
                            randSeq += seq[r.nextInt(seq.length)]
                        }
                        stringTag = "$randSeq а|б|в|г|д|е|ё|ж|з|и|й|к|л|м|н|о|п|р|с|т|у|ф|х|ц|ч|ш|щ|ъ|ы|ь|э|ю"
                        if (mCategories.size !=0){
                            category = mCategories[r.nextInt(mCategories.size)]
                        }
                        else {
                            category = null
                        }
                    }
                    else {
                        stringTag = mgroupSearchTags[r.nextInt(mgroupSearchTags.size)]
                        category = null
                    }
                }

                "Музыка" -> {
                    var musicRandom = Random()
                    musicTagsList.add("музыка")
                    musicTagsList.add("music")
                    musicTagsList.add("official video")
                    musicTagsList.add("хит")
                    musicTagsList.add("music video")
                    musicTagsList.add("клип")
                    musicTagsList.add("clip")
                    musicTagsList.add("премьера")
                    musicTagsList.add("hit")
                    musicTagsList.add("feat")
                    musicTagsList.add("album")
                    musicTagsList.add("official audio")
                    musicTagsList.add("official single")
                    musicTagsList.add("jt")
                    musicTagsList.add("techno")
                    musicTagsList.add("electro")
                    musicTagsList.add("videoclip")
                    musicTagsList.add("videoclip oficial")
                    musicTagsList.add("рэп")
                    musicTagsList.add("rap")
                    musicTagsList.add("hip hop")
                    musicTagsList.add("электронная музыка")
                    musicTagsList.add("клубная музыка")
                    musicTagsList.add("club music")
                    musicTagsList.add("mix")
                    musicTagsList.add("микс")
                    musicTagsList.add("поп музыка")
                    musicTagsList.add("ремикс")
                    musicTagsList.add("альбом")
                    musicTagsList.add("ncs")
                    stringTag = musicTagsList[musicRandom.nextInt(musicTagsList.size)]
                    category = null
                }

                else -> {
                    stringTag = last_taag_value
                    category = null
                }
            }

            return Pair(stringTag, category)
        }

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
                setContentView(R.layout.activity_main2)
                findViewById<CoordinatorLayout>(R.id.cv).setBackgroundColor(Color.rgb(24,28,27))
            }
            else {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)
            }
        }
        else {
            setTheme(android.R.style.Theme_Material_NoActionBar)
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main2)
            findViewById<CoordinatorLayout>(R.id.cv).setBackgroundColor(Color.rgb(24,28,27))
        }
        cursor_theme.close()
        mInstanceActivity = this
        player = findViewById<View>(R.id.youtube_player_view) as YouTubePlayerView
        pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        edit = pref.edit()
        tag_pref = getSharedPreferences("tagSetting", MODE_PRIVATE)


        haveStoragePermission()

        doAsync {
            val getSharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)

            isFirstStart = getSharedPreferences.getBoolean("firstStart", true)

            if (isFirstStart) {
                val i = Intent(this@MainActivity, MyIntro::class.java)
                startActivity(i)
                val e = getSharedPreferences.edit()
                e.putBoolean("firstStart", false)
                e.apply()
            }
        }


        context = this
        dbHelper = DBHelper(context)
        database = dbHelper.writableDatabase
        dbTags = DBTags(context)
        database2 = dbTags.writableDatabase
        dbCategories = DBCategories(this)
        database_category = dbCategories.writableDatabase
        dbSettings = DBSettings(this)
        database_settings = dbSettings.writableDatabase
        dbgroupSearchTags = DBGroupSearchTags(this)
        database_group_search_tags = dbgroupSearchTags.writableDatabase

        //задел на будущий implicit intent
        /*if (this.getIntent().getData() !=null){
            videoId = this.getIntent().getData().toString()
            videoId = videoId.replace("https://youtu.be/", "")
            initYouTubePlayerView()
        }*/

        rv = findViewById(R.id.rv)
        coordinatorLayout = findViewById(R.id.cv)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        rv.layoutManager = mLayoutManager
        rv.itemAnimator = DefaultItemAnimator()
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        cartList = ArrayList()
        ArrayTagsList = ArrayList<String>()
        setTag()
        val itemTouchHelperCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv)
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
        mAdapter = CartListAdapter(this, cartList)

        if (videoId == "b49D0TENyKA") {
            query()
        } else {
            cartList.add(Item(cartListVideoTitle, cartListvideoId))
            initializeAdapter()
            initYouTubePlayerView()
        }
        val menu = findViewById<View>(R.id.arcMenu) as ArcMenu
        //menu.attachToRecyclerView(recyclerView);
        menu.showTooltip(true)
        menu.setToolTipBackColor(Color.TRANSPARENT)
        menu.setToolTipCorner(6f)
        menu.setToolTipPadding(4f)
        menu.setToolTipTextSize(14)
        menu.setToolTipTextColor(Color.BLUE)
        //menu.setIcon(R.mipmap.facebook_w, R.mipmap.github_w);
        menu.setAnim(300, 300, ArcMenu.ANIM_MIDDLE_TO_RIGHT, ArcMenu.ANIM_MIDDLE_TO_RIGHT,
                ArcMenu.ANIM_INTERPOLATOR_ACCELERATE_DECLERATE, ArcMenu.ANIM_INTERPOLATOR_ACCELERATE_DECLERATE)

        val itemCount = MenuItem.ITEM_DRAWABLES.size
        for (i in 0 until itemCount) {
            val item = ImageView(this)
            item.setImageResource(MenuItem.ITEM_DRAWABLES[i])

            menu.addItem(item, null) {
                setTag()
                when (i) {
                    0 -> showDialog(LIST_TAGS)
                    1 -> {
                        val intent2 = Intent(this@MainActivity, SettingsActivity::class.java)
                        startActivity(intent2)
                    }
                    2//переход в плейлист
                    -> {
                        val intent = Intent(this@MainActivity, PlayList::class.java)
                        startActivity(intent)
                    }
                    3//добавление видео в плейлист
                    -> {
                        val contentValues = ContentValues()
                        contentValues.put(DBHelper.KEY_VIDEO_ID, videoId)
                        contentValues.put(DBHelper.KEY_TITLE, videoTitle)
                        contentValues.put(DBHelper.KEY_IMG, "https://i.ytimg.com/vi/$videoId/hqdefault.jpg")
                        database.insert(DBHelper.TABLE_CONTACTS, null, contentValues)

                        MDToast.makeText(context, "Видео сохранено", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show()
                    }
                    4//кнопка скачивания вызывает диалог
                    -> showDialog(IDD_LIST_FORMATS)
                    5//переход в плейлист
                    -> {

                        var textToSend = "https://www.youtube.com/watch?v=$videoId";
                        var mdToast : MDToast = MDToast.makeText(getApplicationContext(), "Ссылка скопирована в буфер обмена", MDToast.LENGTH_LONG)
                        var clipboard : ClipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        var clip : ClipData = ClipData.newPlainText("", textToSend)
                        mdToast.show()
                        clipboard.primaryClip = clip
                    }
                }
            }
        }
        rv.adapter = mAdapter
        initializeAdapter()
        initYouTubePlayerView()
        last_taag_value = ArrayTagsList[tag_pref.getInt(APP_PREFERENCES_TAG, 0)]
    }

    override fun onConfigurationChanged(newConfiguration: Configuration) {
        super.onConfigurationChanged(newConfiguration)
        player.playerUIController.menu!!.dismiss()
    }

    fun setTag() {
        ArrayTagsList.clear()
        val cursor = database2.query(DBTags.TABLE_CONTACTS, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val tagIndex = cursor.getColumnIndex(DBTags.KEY_TAG)
            do {
                db_tag = cursor.getString(tagIndex)
                ArrayTagsList.add(db_tag)
            } while (cursor.moveToNext())
        }
        cursor.close()
        if (ArrayTagsList.isEmpty()) {
            database2.delete(DBTags.TABLE_CONTACTS, null, null)
            val contentValues = ContentValues()
            contentValues.put(DBTags.KEY_TAG, "Без тега")
            database2.insert(DBTags.TABLE_CONTACTS, null, contentValues)
            contentValues.put(DBTags.KEY_TAG, "Музыка")
            database2.insert(DBTags.TABLE_CONTACTS, null, contentValues)
            contentValues.put(DBTags.KEY_TAG, "Поиск по категориям")
            database2.insert(DBTags.TABLE_CONTACTS, null, contentValues)
            contentValues.put(DBTags.KEY_TAG, "Наука и техника")
            database2.insert(DBTags.TABLE_CONTACTS, null, contentValues)
            contentValues.put(DBTags.KEY_TAG, "Юмор")
            database2.insert(DBTags.TABLE_CONTACTS, null, contentValues)
            ArrayTagsList.add("Без тега")
            ArrayTagsList.add("Музыка")
            ArrayTagsList.add("Поиск по категориям")
            ArrayTagsList.add("Наука и техника")
            ArrayTagsList.add("Юмор")
        }
    }

    override fun onBackPressed() {
        if (player.isFullScreen)
            player.exitFullScreen()
        else
            super.onBackPressed()
    }

    fun initYouTubePlayerView() {
        // initPlayerMenu();
        lifecycle.addObserver(player)

        player.initialize({ youTubePlayer ->

            youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                override fun onReady() {
                    loadVideo(youTubePlayer, videoId)
                    youTubePlayer.play()
                    player.enableBackgroundPlayback(true)
                }
            })

            addFullScreenListenerToPlayer(youTubePlayer)

        }, true)
    }

    /* private void initPlayerMenu() {
        player.getPlayerUIController().showMenuButton(true);
        player.getPlayerUIController().getMenu().addItem(
                new com.pierfrancescosoffritti.androidyoutubeplayer.ui.menu.MenuItem("example", R.drawable.ic_settings_24dp, (view) -> Toast.makeText(this, "item clicked", Toast.LENGTH_SHORT).show())
        );
    } */

    private fun loadVideo(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer, videoId: String) {
        if (lifecycle.currentState == Lifecycle.State.RESUMED)
            youTubePlayer.loadVideo(videoId, 0f)
        else
            youTubePlayer.cueVideo(videoId, 0f)

        //setVideoTitle(youTubePlayerView.getPlayerUIController(), videoId);
    }

    private fun addFullScreenListenerToPlayer(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer) {
        player.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                try {
                    fullScreenHelper.enterFullScreen()
                } catch (e: NullPointerException) {
                    // MDToast.makeText(context, e.getMessage(), MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                }

            }

            override fun onYouTubePlayerExitFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                try {
                    fullScreenHelper.exitFullScreen()
                } catch (e: NullPointerException) {
                    // MDToast.makeText(context, e.getMessage(), MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                }

                removeCustomActionFromPlayer()
            }
        })
    }

    private fun removeCustomActionFromPlayer() {
        player.playerUIController.showCustomAction1(false)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar

            // backup of removed item for undo purpose
            val deletedItem = cartList[viewHolder.getAdapterPosition()]
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


    fun restart() {
        super.onStop()
        super.onDestroy()
        super.onCreate(Bundle.EMPTY)
    }

    override fun onResume() {
        super.onResume()
        restart()
    }

    override fun onDestroy() {
        super.onDestroy()
        mInstanceActivity = null
    }

    fun onClick(v: View) {


        when (v.id) {

            R.id.space ->

                initYouTubePlayerView()
        }

    }

    fun onFinishTask() {
        if (cartListVideoTitle != null) {
            cartList.add(Item(cartListVideoTitle, cartListvideoId))
            initializeAdapter()
        }
    }

    fun updateCurrentTag (){
        setTag()
        if (tag_pref.getInt(APP_PREFERENCES_TAG, 0) < ArrayTagsList.size){
            last_taag_value = ArrayTagsList[tag_pref.getInt(APP_PREFERENCES_TAG, 0)]
        }
        else {
            last_taag_value = ArrayTagsList[0]
            val editor2 = tag_pref.edit()
            editor2.putInt(APP_PREFERENCES_TAG, 0)
            editor2.apply()
        }
    }

//for background sound after home pressing
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (!hasFocus)
            super.onRestart()
    }

    //------------------------------   всякая поебота для скачивания видео
    fun haveStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission")
                return true
            } else {

                Log.e("Permission error", "You have asked for permission")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission")
            return true
        }
    }

    fun getDateRange(): DateTime? {
        val cursor = database_settings.query(DBSettings.TABLE_CONTACTS, null, null, null, null, null, null)
        var switcher_new_video_settings_state = 1
        if (cursor.moveToFirst()) {
            val newVideoIndex = cursor.getColumnIndex(DBSettings.KEY_NEW_VIDEO_SWITCHER)
            switcher_new_video_settings_state = cursor.getInt(newVideoIndex)
        }
        cursor.close()
        if (switcher_new_video_settings_state == 1) {
            return DateTime.parseRfc3339("2019-01-13T17:32:18.000Z")
        }
        else {
            return null
        }
    }

    //диалоговое окно для выбора формата скачивания
    override fun onCreateDialog(id: Int): Dialog? {
        when (id) {

            IDD_LIST_FORMATS -> {

                val mCatsName = arrayOf("M4A", "MP4")

                builder = AlertDialog.Builder(this)
                builder.setTitle("Выберите формат") // заголовок для диалога

                builder.setItems(mCatsName) { dialog, item ->
                    // TODO Auto-generated method stub
                    if (mCatsName[item] == "M4A") {
                        getYoutubeDownloadUrl("https://www.youtube.com/watch?v=$videoId")
                        // ClipData clip = ClipData.newPlainText("", "https://www.youtube.com/watch?v=" + videoId);
                    }
                    if (mCatsName[item] == "MP4") {
                        boom()
                    }
                }
                builder.setCancelable(true)
                return builder.create()
            }

            LIST_TAGS -> {

                builder = AlertDialog.Builder(this)
                builder.setTitle("Выберите тег") // заголовок для диалога

                val tmpTagsArray = arrayOfNulls<String>(ArrayTagsList.size)
                for (i in ArrayTagsList.indices) {
                    tmpTagsArray[i] = ArrayTagsList[i].toString()
                }
                if (tag_pref.getInt(APP_PREFERENCES_TAG, 0) < ArrayTagsList.size){
                    builder.setSingleChoiceItems(tmpTagsArray, tag_pref.getInt(APP_PREFERENCES_TAG, 0)) { dialog, item ->
                        // TODO Auto-generated method stub
                        last_taag_value = ArrayTagsList[item]
                        dialog.cancel()
                        val editor2 = tag_pref.edit()
                        editor2.putInt(APP_PREFERENCES_TAG, item)
                        editor2.apply()
                    }
                }
                else {
                    builder.setSingleChoiceItems(tmpTagsArray, 0) { dialog, item ->
                        // TODO Auto-generated method stub
                        last_taag_value = ArrayTagsList[item]
                        dialog.cancel()
                        val editor2 = tag_pref.edit()
                        editor2.putInt(APP_PREFERENCES_TAG, item)
                        editor2.apply()
                    }
                }
                builder.setCancelable(true)
                return builder.create()
            }
            else -> return null
        }
    }

    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        object : YouTubeExtractor(this) {
            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    finish()
                    return
                }
                // Iterate over itags
                var i = 0
                var itag: Int
                while (i < ytFiles.size()) {
                    itag = ytFiles.keyAt(i)
                    // ytFile represents one file with its url and meta data
                    val ytFile = ytFiles.get(itag)

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.format.height == -1 || ytFile.format.height >= 360) {
                        addButtonToMainLayout(vMeta.title, ytFile)
                    }
                    i++
                }
            }
        }.extract(youtubeLink, true, false)
    }

    private fun addButtonToMainLayout(videoTitle: String, ytfile: YtFile) {
        // Display some buttons and let the user choose the format
        var btnText = if (ytfile.format.height == -1)
            "Audio " +
                    ytfile.format.audioBitrate + " kbit/s"
        else
            ytfile.format.height.toString() + "p"
        btnText += if (ytfile.format.isDashContainer) " dash" else ""
        var filename: String
        if (videoTitle.length > 55) {
            filename = videoTitle.substring(0, 55) + "." + ytfile.format.ext
        } else {
            filename = videoTitle + "." + ytfile.format.ext
        }
        filename = filename.replace("[\\\\><\"|*?%:#/]".toRegex(), "")
        downloadFromUrl(ytfile.url, videoTitle, filename)
    }

    private fun downloadFromUrl(youtubeDlUrl: String, downloadTitle: String, fileName: String) {
        val uri = Uri.parse(youtubeDlUrl)
        val request = DownloadManager.Request(uri)
        request.setTitle(downloadTitle)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        //        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //        manager.enqueue(request);

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadId = manager.enqueue(request)


        Thread(Runnable {
            var downloading = true

            while (downloading) {

                val q = DownloadManager.Query()
                q.setFilterById(downloadId)

                val cursor = manager.query(q)
                cursor.moveToFirst()
                val bytes_downloaded = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }

                val dl_progress = (bytes_downloaded * 100L / bytes_total).toInt()


                cursor.close()
            }
        }).start()


    }

    private fun boom() {
        val count = pref.getInt("count", 1)
        edit.putInt("count", 1)
        val boomurl = videoId
        val url = "http://dyall.herokuapp.com/api/info?url=$boomurl"
        Log.d("URL", url)
        val requestQueue = Volley.newRequestQueue(this@MainActivity)
        val stringRequest = object : StringRequest(Request.Method.GET, url, Response.Listener { response ->
            progressDialog.dismiss()
            try {
                val jsonobject = JSONObject(response)
                val js = jsonobject.getJSONObject("info")
                title_downloaded_video = js.getString("title")
                title_downloaded_video = title_downloaded_video.replace(' ', '_')
                ext = js.getString("ext")
                download(js.getString("url").toString())


                //  Toast.makeText(MainActivity.this, js.getString("url").toString(), Toast.LENGTH_SHORT).show();
            } catch (e: JSONException) {
                e.printStackTrace()
                //  Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }, Response.ErrorListener {
            progressDialog.dismiss()
            MDToast.makeText(context, "Unknown error. Try again!", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = ""
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                //params.put("authorization", "token ce3fe9a203703c7ea3da8727ff8fbafec8ddbf44");
                return params
            }
        }
        requestQueue.add(stringRequest)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Parsing the file buddy ....")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

    }

    private fun download(url: String) {
        val uri = Uri.parse(url)
        val downloadReference = DownloadData(uri)
        val mdToast = MDToast.makeText(this, "Download started...", MDToast.LENGTH_LONG)
        mdToast.show()
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadReference == reference) {
                    MDToast.makeText(this@MainActivity, "Download completed!", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show()

                }
            }
        }
        registerReceiver(receiver, filter)
    }

    private fun DownloadData(uri: Uri): Long {
        val downloadReference: Long
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedOverRoaming(true)
        request.setTitle(title_downloaded_video)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDescription("Downloading...")
        request.setDestinationInExternalPublicDir(
                "/LuckyTube", "$title_downloaded_video.$ext")
        downloadReference = downloadManager.enqueue(request)

        return downloadReference
    }

    //-------------------------------------- всякая поебота для скачивания видео

    //------------тосты
    fun showInfoToast(view: View) {
        val mdToast = MDToast.makeText(this, "This is an INFO Toast", MDToast.LENGTH_LONG)
        mdToast.show()
    }

    fun showSuccessToast(view: View) {
        MDToast.makeText(this, "This is a SUCCESS Toast", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show()
    }

    fun showWarningToast(view: View) {
        MDToast.makeText(this, "This is a WARNING Toast", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show()
    }

    fun showErrorToast(view: View) {
        MDToast.makeText(this, "This is an ERROR Toast", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show()
    }

    companion object {

        val APP_PREFERENCES_TAG = "tagSetting_key"
        lateinit var tag_pref: SharedPreferences

        internal lateinit var player: YouTubePlayerView
        lateinit var database2: SQLiteDatabase
        lateinit var dbTags: DBTags
        private var mAdapter: CartListAdapter? = null
        var cartListvideoId: String? = null
        var videoId = "b49D0TENyKA"
        var cartListVideoTitle: String? = null
        var videoTitle: String? = "ШКОЛЬНИК ПЫТАЛСЯ ВЗЛОМАТЬ МОЙ КОМП.. но обоср@лся"
        var preview: Bitmap? = null
        lateinit var cartList: MutableList<Item>
        lateinit var database: SQLiteDatabase
        lateinit var dbHelper: DBHelper
        lateinit var database_category: SQLiteDatabase
        lateinit var dbCategories: DBCategories
        lateinit var database_settings: SQLiteDatabase
        lateinit var dbSettings: DBSettings
        var mCategories = ArrayList<String>()
        lateinit var database_group_search_tags: SQLiteDatabase
        lateinit var dbgroupSearchTags: DBGroupSearchTags
        var mgroupSearchTags = ArrayList<String>()
        private var mInstanceActivity: MainActivity? = null
        var isFirstStart: Boolean = false
        lateinit var ArrayTagsList: ArrayList<String>
        lateinit var db_tag: String
        var last_taag_value = "Без тега"

        fun query() {
            Machine.something()
        }

        fun getmInstanceActivity(): MainActivity? {
            return mInstanceActivity
        }

        fun initializeAdapter() {
            mAdapter!!.notifyDataSetChanged()
            //rv.scrollToPosition(0);
        }

        val duration: String
            get() {
                when (RVAdapter.duration.selectedItem.toString()) {
                    "Короткие" -> return "short"
                    "Длинные" -> return "long"
                    else -> return "any"
                }
            }
    }
    ////////////--------

}
