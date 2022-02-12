package com.codingwithmitch.kotlinrecyclerviewexample
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
import com.codingwithmitch.kotlinrecyclerviewexample.models.Device
import com.google.gson.GsonBuilder




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
        addDataSet();
        mqttManager = MQTTmanager(connectionParams, applicationContext);
        mqttManager!!.connect();
        mainHandler.postDelayed(backgroundTask, 10000);
//        workManager = WorkManager.getInstance(this)
//        startPeriodicRequest();



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
        var  dataToSend:String= toJson(allMeasurements);
        return dataToSend
    }

    private fun toJson(obj:Any):String{
        val gsonObj = GsonBuilder().create();
        val jsonPretty: String = gsonObj.toJson(obj);
        return jsonPretty;

    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onResume() {
//        if () {
//
//            handler!!.postDelayed(Runnable {
//                handler!!.postDelayed(runnable, delay.toLong())
//
//                if (mqttManager.connected) {
//
//                    val msg = prepareData()
//                    Toast.makeText(
//                        this@MainActivity, "Measurements are Updated",
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//
//                    if (msg != null) {
//                        mqttManager.publish(msg)
//
//                        Handler().postDelayed({
//                            Toast.makeText(
//                                this@MainActivity, "Measurements are Published: $packetCounter",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }, 3000)
//
//                    };
//
//
//                } else {
//                    mqttManager.connect()
//                }
//
//
//
//            }.also { runnable = it }, delay.toLong())
//        }
//            super.onResume()
//
//    }




}























