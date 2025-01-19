package com.example.rechic.database.local

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng


class Converters {

    @TypeConverter
    fun fromLatLng(location: LatLng): String {
        return "${location.latitude},${location.longitude}"
    }


    @TypeConverter
    fun toLatLng(latLngString: String): LatLng {
        val parts = latLngString.split(",")
        return LatLng(parts[0].toDouble(), parts[1].toDouble())
    }
}