package com.codingwithmitch.kotlinrecyclerviewexample.models
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDateTime


class Xam_Dummy constructor(deviceId:String) {
    public val deviceId:String=deviceId;
    private val channel0 = mutableMapOf(
        "channelIndex" to 0,
        "value" to mutableMapOf( "value" to 0, "numDigits" to 0, "unit" to "PPM" ),
        "timestamp" to 1643574475,
        "gasName" to "ch4",
        "valueState" to "Valid",
        "alarmState" to mutableMapOf(  "category" to "CatNone", "ackState" to "NotAcknowledgable"  )

    );
    private val channel1 = mutableMapOf(
        "channelIndex" to 1,
        "value" to mutableMapOf( "value" to 0, "numDigits" to 0, "unit" to "PPM" ),
        "timestamp" to 1643574475,
        "gasName" to "ch4+",
        "valueState" to "Valid",
        "alarmState" to mutableMapOf(  "category" to "CatNone", "ackState" to "NotAcknowledgable"  )

    );
    private val channel2 = mutableMapOf(
        "channelIndex" to 2,
        "value" to mutableMapOf( "value" to 0, "numDigits" to 0, "unit" to "PPM" ),
        "timestamp" to 1643574475,
        "gasName" to "CO",
        "valueState" to "Valid",
        "alarmState" to mutableMapOf(  "category" to "CatNone", "ackState" to "NotAcknowledgable"  )

    );
    var measurements:List<Map<String, Any>> = ArrayList();

    @RequiresApi(Build.VERSION_CODES.O)
    public fun createMeasurements(): List<Map<String, Any>> {
        val timestamp = LocalDateTime.now();
        channel0["value"] = mutableMapOf( "value" to (0..9).random()*.88, "numDigits" to 0, "unit" to "PPM" )
        channel1["value"] = mutableMapOf( "value" to (0..8).random()*.95, "numDigits" to 0, "unit" to "PPM" )
        channel2["value"] = mutableMapOf( "value" to (0..10).random()*.95, "numDigits" to 0, "unit" to "PPM" )
        channel0["timestamp"] = timestamp;
        channel1["timestamp"] = timestamp;
        channel2["timestamp"] = timestamp;

        measurements= listOf(channel0, channel1, channel2);
        return measurements as List<Map<String, Any>>;

    }

    public fun toJson():String{
        val gsonObj = GsonBuilder().setPrettyPrinting().create();
        val jsonPretty: String = gsonObj.toJson(measurements);
        return jsonPretty;

    }
}