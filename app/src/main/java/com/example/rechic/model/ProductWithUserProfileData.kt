package com.example.rechic.model

import android.os.Parcelable
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.database.local.entities.UserProfileEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductWithUserProfile(
    val product: ProductEntity,
    val userProfile: UserProfileEntity,
) : Parcelable