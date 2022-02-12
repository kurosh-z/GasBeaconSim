package com.codingwithmitch.kotlinrecyclerviewexample

import android.os.Build
import androidx.annotation.RequiresApi
import com.codingwithmitch.kotlinrecyclerviewexample.models.Airboss
import com.codingwithmitch.kotlinrecyclerviewexample.models.Device
import com.codingwithmitch.kotlinrecyclerviewexample.models.Xam_Dummy

class DataSource{

    companion object{

        @RequiresApi(Build.VERSION_CODES.O)
        fun createDataSet(): ArrayList<Device>{
            val xamdummy = Xam_Dummy("xam01_sim");
            xamdummy.createMeasurements();

            val list = ArrayList<Device>()
            list.add(
                Device(
                    xamdummy.deviceId,
                    xamdummy.toJson(),
                    xamdummy.measurements
                )
            )
            val Airboss_dev = Airboss("Airboss01_sim");
            Airboss_dev.createMeasurements();
            list.add(
                Device(
                    Airboss_dev.deviceId,
                    Airboss_dev.toJson(),
                    Airboss_dev.measurements
                )
            )


            return list
        }
    }
}