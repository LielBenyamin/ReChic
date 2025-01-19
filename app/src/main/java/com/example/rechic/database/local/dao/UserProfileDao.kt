package com.example.rechic.database.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.database.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(products: List<UserProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE userUid = :userUid")
    suspend fun getUserProfileByUid(userUid: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE userUid = :userUid")
    fun getUserProfileByUidFlow(userUid: String): Flow<UserProfileEntity?>

    @Query("DELETE FROM user_profiles WHERE userUid = :userUid")
    suspend fun deleteUserProfileByUid(userUid: String)

    @Query("DELETE FROM user_profiles")
    suspend fun clearAllUserProfiles()
}