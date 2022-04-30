package com.example.sleepapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.settings_layout.*
import kotlin.math.max

class Settings(MainActivity: MainActivity) : Fragment() {

    private lateinit var ma : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_layout, container, false)
    }


    init {
        ma = MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set default value, once I implement the database this will be store there and remember
        //the users choice

        sensitivityBar.progress = 5;
        lightSensitivityBar.progress = 5;
        motionSensitivityBar.progress = 5;

        sensitivityBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                Log.d("seek bar", "seek bar current value: " + sensitivityBar.progress)
                ma.reqConfidenceLvl = 85 - sensitivityBar.progress;
            }
            override fun onStartTrackingTouch(seek: SeekBar) {
                // don't care about this
            }
            override fun onStopTrackingTouch(seek: SeekBar) {
                // don't care about this
            }
        })
        lightSensitivityBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                Log.d("seek bar", "seek bar current value: " + lightSensitivityBar.progress)
                ma.reqLightLvl = 15 + lightSensitivityBar.progress;
            }
            override fun onStartTrackingTouch(seek: SeekBar) {
                // don't care about this
            }
            override fun onStopTrackingTouch(seek: SeekBar) {
                // don't care about this
            }
        })
        motionSensitivityBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                Log.d("seek bar", "seek bar current value: " + motionSensitivityBar.progress)
                ma.reqMotionLvl = 15 + motionSensitivityBar.progress;
            }
            override fun onStartTrackingTouch(seek: SeekBar) {
                // don't care about this
            }
            override fun onStopTrackingTouch(seek: SeekBar) {
                // don't care about this
            }
        })

        alarmBtn.setOnClickListener() {

        }
    }

}