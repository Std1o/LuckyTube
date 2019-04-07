package com.akhilpatoliya.videoplayerdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import com.github.paolorotolo.appintro.AppIntro

/**
 * Created by HP on 10/23/2016.
 */
class MyIntro : AppIntro() {
    // Please DO NOT override onCreate. Use init
    override fun init(savedInstanceState: Bundle?) {

        //adding the three slides for introduction app you can ad as many you needed
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro1))
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro2))

        // Show and Hide Skip and Done buttons
        showStatusBar(false)
        setFlowAnimation()
    }

    override fun onSkipPressed() {
        // Do something here when users click or tap on Skip button.
        Toast.makeText(applicationContext,
                getString(R.string.app_intro_skip), Toast.LENGTH_SHORT).show()
        val i = Intent(applicationContext, MainActivity::class.java)
        startActivity(i)
    }

    override fun onNextPressed() {
        super.onNextPressed()
    }

    override fun onDonePressed() {
        // Do something here when users click or tap tap on Done button.
        finish()
    }

    override fun onSlideChanged() {
        // Do something here when slide is changed
    }
}