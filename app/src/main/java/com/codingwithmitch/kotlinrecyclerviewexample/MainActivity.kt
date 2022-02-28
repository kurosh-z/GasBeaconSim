package com.codingwithmitch.kotlinrecyclerviewexample
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingwithmitch.kotlinrecyclerviewexample.manager.MQTTConnectionParams
import com.codingwithmitch.kotlinrecyclerviewexample.manager.MQTTmanager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import android.widget.Toast
import com.codingwithmitch.kotlinrecyclerviewexample.manager.GpsUtils
import com.codingwithmitch.kotlinrecyclerviewexample.models.Device
import com.google.android.gms.location.*
import com.google.gson.GsonBuilder
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.tasks.OnSuccessListener
import androidx.annotation.NonNull

import android.annotation.SuppressLint
import android.app.Activity

import android.content.Intent








class MainActivity : AppCompatActivity() {

//
//    private lateinit var workManager: WorkManager
//    private lateinit var periodicReq: PeriodicWorkRequest
    var handler: Handler? = Handler()
    var runnable: Runnable? = null
    var delay = 10000
    var packetCounter:Long =0
    var host = "tcp://" + "212.227.175.162" + ":1883"
    var topic = "android/direct_to_cloud/sim"
    var connectionParams = MQTTConnectionParams("mqtt",host,topic,"mqtt","mqtt123")
    private lateinit var mqttManager:MQTTmanager;
    private lateinit var data: ArrayList<Device>
    private lateinit var deviceAdapter: DeviceRecyclerAdapter
    private val mainHandler = Handler()



    private lateinit var  mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val locationRequestCode = 1000
    private var latitude :kotlin.Double = 0.0
    private  var longitude:kotlin.Double = 0.0
    private lateinit var locationRequest: LocationRequest
    private var isGPS = false
    private var isContinue= true







    private val backgroundTask = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            //do your background task

            if (mqttManager.connected) {
                val msg = prepareData()
                if (msg != null) {
                    mqttManager.publish(msg)
                    Toast.makeText(
                        this@MainActivity, "Measurements are Published: $packetCounter",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
            else{
                mqttManager.connect()
            }

            mainHandler.postDelayed(this, 10000)
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        //init gps

        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        locationRequest.interval = 10 * 1000; // 10 seconds
        locationRequest.fastestInterval = 5 * 1000; // 5 seconds

        GpsUtils(this.applicationContext).turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
            }
        })

        if (!isGPS) {
            Toast.makeText(this@MainActivity, "Please turn on GPS", Toast.LENGTH_SHORT).show();
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude

                    }
                }
            }
        }
        getLocation();

        //finished gps stuff






        addDataSet();
        mqttManager = MQTTmanager(connectionParams, applicationContext);
        mqttManager!!.connect();
        mainHandler.postDelayed(backgroundTask, 10000);
//        workManager = WorkManager.getInstance(this)
//        startPeriodicRequest();



    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                AppConstants.LOCATION_REQUEST
            )
        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            } else {
                mFusedLocationClient.lastLocation.addOnSuccessListener(
                    this@MainActivity
                ) { location: Location? ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude

                    } else {
                        mFusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            null
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            null
                        )
                    } else {
                        mFusedLocationClient.lastLocation.addOnSuccessListener(
                            this@MainActivity
                        ) { location: Location? ->
                            if (location != null) {
                                latitude = location.latitude
                                longitude = location.longitude

                            } else {
                                mFusedLocationClient.requestLocationUpdates(
                                    locationRequest,
                                    locationCallback,
                                    null
                                )
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDataSet(){
         data = DataSource.createDataSet()
        deviceAdapter.submitList(data)
    }


    private fun initRecyclerView(){

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecorator)
            deviceAdapter = DeviceRecyclerAdapter()
            adapter = deviceAdapter
        }

//
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareData(): String {
        addDataSet()
        packetCounter++
        deviceAdapter.notifyDataSetChanged()

        var allMeasurements= mutableMapOf<String, Any>();

        val itr = data.listIterator();
        while (itr.hasNext()) {
            var nextDevice = itr.next()
            allMeasurements.put(nextDevice.deviceId, nextDevice.measurements)

        }
        allMeasurements.put("packet_Counter", packetCounter)
        allMeasurements.put("latitude", latitude)
        allMeasurements.put("longitude", longitude)
        var  dataToSend:String= toJson(allMeasurements);
        return dataToSend
    }

    private fun toJson(obj:Any):String{
        val gsonObj = GsonBuilder().create();
        val jsonPretty: String = gsonObj.toJson(obj);
        return jsonPretty;

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true // flag maintain before get location
            }
        }
    }







}























