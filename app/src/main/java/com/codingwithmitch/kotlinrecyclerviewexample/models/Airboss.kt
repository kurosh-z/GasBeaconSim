package com.codingwithmitch.kotlinrecyclerviewexample.models
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDateTime


class Airboss constructor(deviceId:String) {
    public val deviceId:String=deviceId;
    private val channel0 = mutableMapOf(
        "value" to mutableMapOf( "value" to 0, "unit" to "MBAR" ),
        "timestamp" to 1643574475,
        "gasName" to "Air",
        "valueState" to "Valid",
        "alarmState" to mutableMapOf(  "category" to "CatNone", "ackState" to "NotAcknowledgable"  )
    );

    var measurements:List<Map<String, Any>> = ArrayList();

    @RequiresApi(Build.VERSION_CODES.O)
    public fun createMeasurements(): List<Map<String, Any>> {
        val timestamp = LocalDateTime.now();
        channel0["value"] = mutableMapOf( "value" to (0..200).random(), "unit" to "MBAR" )
        channel0["timestamp"] = timestamp;
        measurements= listOf(channel0);
        return measurements as List<Map<String, Any>>;

    }

    public fun toJson():String{
        val gsonObj = GsonBuilder().setPrettyPrinting().create();
        val jsonPretty: String = gsonObj.toJson(measurements);
        return jsonPretty;

    }
}