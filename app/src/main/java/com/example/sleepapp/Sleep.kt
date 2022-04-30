package com.example.sleepapp

import android.os.Build
import android.os.Bundle
import android.transition.*
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.sleep_layout.*
import android.view.animation.Animation

import android.view.animation.Animation.AnimationListener
import androidx.room.Room
import com.example.sleepapp.ui.main.Database
import java.text.SimpleDateFormat
import java.util.*


//import kotlinx.android.synthetic.main.sleep_layout.*

class Sleep(MainActivity: MainActivity) : Fragment() {

    private lateinit var ma : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sleep_layout, container, false)
    }

    init {
        ma = MainActivity
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //logic of animation
        var animationOut = AnimationUtils.loadAnimation(this.context, R.anim.anim_slide_out)
        var animationIn = AnimationUtils.loadAnimation(this.context, R.anim.anim_slide_in)

        //logic for sleep tracking (and animation a bit)
        var isTracking: Boolean = false

        collectButton.setOnClickListener() {
            if(isTracking) {
                animationOut.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        sunMoon.setImageResource(R.drawable.sun)
                        sunMoon.startAnimation(animationIn)
                    }
                })
                sunMoon.startAnimation(animationOut)
                isTracking = false
                ma.sensors.pauseRecording()
                collectButton.setText("Start Sleep Tracking")
            }
            else{
                animationOut.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        sunMoon.setImageResource(R.drawable.moon)
                        sunMoon.startAnimation(animationIn)
                    }
                })
                sunMoon.startAnimation(animationOut)
                isTracking = true
                ma.sensors.startRecording()
                collectButton.setText("Stop Sleep Tracking")
            }

        }

    }



}