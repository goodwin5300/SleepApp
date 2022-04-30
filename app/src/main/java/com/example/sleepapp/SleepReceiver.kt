package com.example.sleepapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent
import java.io.File

class SleepReceiver : BroadcastReceiver() {
    //content provider

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "received something")

        if (SleepSegmentEvent.hasEvents(intent)) {
            val events =
                intent?.let { SleepSegmentEvent.extractEvents(it) }

            Log.d(TAG, "Logging SleepSegmentEvents")

            if (events != null) {
                for (event in events) {
                    Log.d(TAG, "${event.startTimeMillis} to ${event.endTimeMillis} with status ${event.status}")
                }
            }

        } else if (SleepClassifyEvent.hasEvents(intent)) {
            val events = intent?.let { SleepClassifyEvent.extractEvents(it) }

            Log.d(TAG, "Logging SleepClassifyEvents")

            if (events != null) {
                for (event in events) {
                    Log.i(TAG, "Confidence: ${event.confidence} - Light: ${event.light} - Motion: ${event.motion}")
                    sensors.onSleepClassifyEvent(event);
                }
            }
        }

    }


    companion object {
        private const val TAG = "SLEEP_RECEIVER"
        private lateinit var sensors: Sensors

        //create pending intent to subscribe to sleep PAPI
        fun createPendingIntent(context: Context, mainActivity: MainActivity): PendingIntent {
            Log.d("SleepAPI", "creating pending intent");
            val intent = Intent(context, SleepReceiver::class.java)

            sensors = mainActivity.sensors;

            return PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
        }


    }
}