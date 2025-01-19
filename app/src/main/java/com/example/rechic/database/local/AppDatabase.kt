package com.example.rechic.database.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rechic.database.local.dao.ProductDao
import com.example.rechic.database.local.dao.UserProfileDao
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.database.local.entities.UserProfileEntity

@TypeConverters(Converters::class)
@Database(
    entities = [UserProfileEntity::class, ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun productDao(): ProductDao
}