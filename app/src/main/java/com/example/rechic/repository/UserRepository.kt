package com.example.rechic.repository

import com.example.rechic.database.local.dao.UserProfileDao
import com.example.rechic.database.local.entities.UserProfileEntity
import com.example.rechic.database.remote.UserFirebaseDB

class UserRepository(
    private val userFirebaseDB: UserFirebaseDB,
    private val userDao: UserProfileDao,
) {

    suspend fun checkIfUsernameExists(username: String): Boolean {
        return userFirebaseDB.checkIfUsernameExists(username)
    }

    suspend fun saveUser(user: UserProfileEntity) {
        userFirebaseDB.saveUser(user)
    }

    suspend fun updateUserProfileFields(
        userId: String,
        keys: List<String>,
        values: List<Any>,
        userProfileCopy: UserProfileEntity,
    ) {
        val fieldsToUpdate = keys.zip(values).toMap()
        userFirebaseDB.updateUserProfileFields(userId, fieldsToUpdate)
        userDao.insert(userProfileCopy)
    }

    suspend fun syncUsers() {
        val firestoreUsers = userFirebaseDB.fetchAllUsers()
        userDao.insertUsers(firestoreUsers)
    }

    fun getUserProfileFlow(uid: String) = userDao.getUserProfileByUidFlow(uid)

    fun getAllUsersFlow() = userDao.getAllUsersFlow()

    suspend fun getCurrentUserProfile(uid: String) = userDao.getUserProfileByUid(uid)
}