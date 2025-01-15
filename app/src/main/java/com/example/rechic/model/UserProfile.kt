package com.example.rechic.model

import com.google.android.gms.maps.model.LatLng

data class UserProfile(
    val userName: String,
    val email: String,
    val profileImageUrl: String,
    val location: LatLng,
    val phoneNumber : String,
)