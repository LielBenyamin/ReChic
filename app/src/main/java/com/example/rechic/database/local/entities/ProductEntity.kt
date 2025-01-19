package com.example.rechic.database.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "products")
@Parcelize
data class ProductEntity(
    @PrimaryKey
    val productDocumentId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imgUrl: String,
    val ownerUid: String,
) : Parcelable {
    constructor() : this("", "", "", 0.0, "", "")
}