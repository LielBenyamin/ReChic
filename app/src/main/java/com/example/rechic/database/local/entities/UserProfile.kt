package com.example.rechic.database.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Entity(tableName = "user_profiles")
@Parcelize
data class UserProfileEntity(
    @PrimaryKey val userUid: String,
    val userName: String,
    val email: String,
    val profileImageUrl: String,
    val location: LatLng,
    val phoneNumber: String,
) : Parcelable