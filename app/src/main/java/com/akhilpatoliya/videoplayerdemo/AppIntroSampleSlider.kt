package com.akhilpatoliya.videoplayerdemo


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by HP on 10/23/2016.
 */
class AppIntroSampleSlider : Fragment() {

    private var layoutResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments!!.containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = arguments!!.getInt(ARG_LAYOUT_RES_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    companion object {
        //Layout id
        private val ARG_LAYOUT_RES_ID = "layoutResId"

        fun newInstance(layoutResId: Int): AppIntroSampleSlider {
            val sampleSlide = AppIntroSampleSlider()

            val bundleArgs = Bundle()
            bundleArgs.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            sampleSlide.arguments = bundleArgs

            return sampleSlide
        }
    }

}