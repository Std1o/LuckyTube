package com.akhilpatoliya.videoplayerdemo;

/**
 * Created by ravi on 26/09/17.
 */

import android.graphics.Bitmap;

public class Item {
    String title;
    String videoId;

    Item(String title, String videoId) {
        this.title = title;
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoId() {
        return videoId;
    }

}