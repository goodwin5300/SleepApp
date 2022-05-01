package com.example.sleepapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.example.sleepapp.ui.main.SectionsPagerAdapter
import com.example.sleepapp.databinding.ActivityMainBinding
import com.example.sleepapp.ui.main.Database
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

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
    public var reqConfidenceLvl: Int = 80 //level of confidence required to assume the user is asleep
    public var reqLightLvl: Int = 20 //level of light required to assume the user is asleep
    public var reqMotionLvl: Int = 20 //level of motion required to assume the user is asleep

     public lateinit var interpreter: Interpreter;

    public lateinit var db: Database; //to access the db

    @RequiresApi(Build.VERSION_CODES.O)
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

        interpreter = Interpreter(loadModelFile(this), null, )

        //setup the sensor manager
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

        db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "database-name"
        ).allowMainThreadQueries().build()
        Log.d("Database: ", "got database");
        //example of inserting data to the database with test data
        /*val userDao = db.userDao()
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm")

        val n1 = Database.Night()
        n1.date = "04/22/2022"
        n1.asleep = "22:30"
        n1.awake = "08:30"
        n1.startTracking = "20:30"

        val n2 = Database.Night()
        n2.date = "04/23/2022"
        n2.asleep = "22:30"
        n2.awake = "08:30"
        n2.startTracking = "20:30"

        val n3 = Database.Night()
        n3.date = "04/24/2022"
        n3.asleep = "22:30"
        n3.awake = "08:30"
        n3.startTracking = "20:30"

        userDao.insert(n1);
        userDao.insert(n2);
        userDao.insert(n3);*/
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
        if(Build.VERSION.SDK_INT >= 29)
            sleepRequestManager.subscribeToSleepUpdates();
        else
            sensors.startTracking();
    }

    public fun unSubscribeToSleepEvents() {
        if(Build.VERSION.SDK_INT >= 29)
            sleepRequestManager.unsubscribeFromSleepUpdates();
        else
            sensors.stopTracking();
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

    @Throws(IOException::class)
    fun loadModelFile(context: Context): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd("model.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }

    fun randomForestInterface(
        acclX: Float,
        acclY: Float,
        acclZ: Float,
        variance: Float,
        prox: Float,
        light: Float
    ): Array<FloatArray> {
        val input = FloatArray(6)
        input[0] = acclX
        input[1] = acclY
        input[2] = acclZ
        input[3] = variance
        input[4] = prox
        input[5] = light
        var output: Float = 0F
        interpreter.run(input, output);
        return Array(1) { FloatArray(1) }
    }

    fun passDataToAI(
        acclReadings: FloatArray,
        proxReading: Float,
        lightReading: Float,
        acclVariance: Float
    ) {
        var result = randomForestInterface(acclReadings[0], acclReadings[1], acclReadings[2], acclVariance, proxReading, lightReading);
        confidenceLvl = result[0][0].toInt()
        checkConfidenceLvl()
    }

}