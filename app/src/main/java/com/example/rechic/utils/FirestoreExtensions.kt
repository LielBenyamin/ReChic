package com.example.rechic.utils

import com.example.rechic.model.UserProfile
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot


fun DocumentSnapshot.toUserProfile(): UserProfile {
    val userName = getString(UserProfile::userName.name)!!
    val email = getString(UserProfile::email.name)!!
    val profileImageUrl = getString(UserProfile::profileImageUrl.name)!!
    val phoneNumber = getString(UserProfile::phoneNumber.name)!!
    val locationMap = get(UserProfile::location.name) as Map<String, Double>
    val lat = locationMap["latitude"]!!
    val lng = locationMap["longitude"]!!
    val location = LatLng(lat, lng)
    return UserProfile(
        userName = userName,
        email = email,
        profileImageUrl = profileImageUrl,
        location = location,
        phoneNumber = phoneNumber,
    )
}
