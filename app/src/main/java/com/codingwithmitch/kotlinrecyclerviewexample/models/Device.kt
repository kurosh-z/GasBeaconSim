package com.codingwithmitch.kotlinrecyclerviewexample.models

import android.location.GnssMeasurement
import com.google.gson.GsonBuilder



data class Device(var deviceId: String, var payload: String, var measurements:List<Map<String, Any>>) {

    override fun toString(): String {
        return "BlogPost(title='$deviceId', username='$payload')"
    }

//    public fun toJson():String{
//        val gsonObj = GsonBuilder().setPrettyPrinting().create();
//        val jsonPretty: String = gsonObj.toJson(payload);
//        return jsonPretty;
//
//    }

}
























