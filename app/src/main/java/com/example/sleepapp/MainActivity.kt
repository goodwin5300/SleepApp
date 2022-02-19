package com.example.sleepapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sleepapp.ui.main.SectionsPagerAdapter
import com.example.sleepapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public val sensors by lazy {
        Sensors(MainActivity = this)
    }
    public lateinit var mSensorManager : SensorManager
    private val sleepRequestManager by lazy{
        SleepRequestsManager(this, this)
    }

    public var confidenceLvl: Int = 0 //current confidence level that the user is asleep
    public var reqConfidenceLvl: Int = 100 //level of confidence required to assume the user is asleep

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, this)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        //setup the sensor manager
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    //method to open the settings
    private fun requestActivityRecognitionPermission() {

        Log.d("permission", "passing intent for settings");

        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(intent)
    }

    //function to request permission and subscribe to sleep updates
    private fun permissionRequester() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
                Log.d("permission", "requesting permission")
                ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1)
        }
        else {
            Log.d("permission", "has permission")
        }
    }


    //function to obtain permission to track sleep data
    public fun requestPerm() {
        Log.d("permission", "Starting requesting permission");
        permissionRequester()
        sleepRequestManager.requestSleepUpdates(requestPermission = {
            permissionRequester()
        })
    }

    public fun subscribeToSleepEvents() {
        sleepRequestManager.subscribeToSleepUpdates();
    }

    public fun unSubscribeToSleepEvents() {
        sleepRequestManager.unsubscribeFromSleepUpdates();
    }

    public fun checkConfidenceLvl() {
        if(confidenceLvl >= reqConfidenceLvl) {
            //puase media and turn off tracking, log in database
            //for now just log when media is paused
            //TODO: add database logging and pausing media
            sensors.pauseRecording()
            Log.d("sleep tracking", "determined user asleep, confidence level: " + confidenceLvl)
        }
    }

}