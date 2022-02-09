package com.example.sleepapp

import android.os.Build
import android.os.Bundle
import android.transition.*
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




//import kotlinx.android.synthetic.main.sleep_layout.*

class Sleep : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sleep_layout, container, false)
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
            }
        }

    }



}