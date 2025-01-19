package com.example.rechic.utils

import com.example.rechic.database.local.entities.UserProfileEntity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot


fun DocumentSnapshot.toUserProfile(): UserProfileEntity {
    val userName = getString(UserProfileEntity::userName.name)!!
    val email = getString(UserProfileEntity::email.name)!!
    val profileImageUrl = getString(UserProfileEntity::profileImageUrl.name)!!
    val phoneNumber = getString(UserProfileEntity::phoneNumber.name)!!
    val locationMap = get(UserProfileEntity::location.name) as Map<String, Double>
    val lat = locationMap["latitude"]!!
    val lng = locationMap["longitude"]!!
    val location = LatLng(lat, lng)
    val userUid = id
    return UserProfileEntity(
        userName = userName,
        email = email,
        profileImageUrl = profileImageUrl,
        location = location,
        phoneNumber = phoneNumber,
        userUid = userUid,
    )
}
