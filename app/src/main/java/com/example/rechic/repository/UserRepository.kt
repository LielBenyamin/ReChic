package com.example.rechic.repository

import com.example.rechic.database.remote.UserFirebaseDB
import com.example.rechic.model.UserProfile

class UserRepository(private val userFirebaseDB: UserFirebaseDB) {

    suspend fun checkIfUsernameExists(username: String): Boolean {
        return userFirebaseDB.checkIfUsernameExists(username)
    }

    suspend fun saveUser(userId: String, user: UserProfile) {
        userFirebaseDB.saveUser(userId, user)
    }

    suspend fun getCurrentUserLoggedInProfile(): UserProfile? {
        return userFirebaseDB.getUserProfile()
    }

    suspend fun updateUserProfileFields(userId: String, keys: List<String>, values: List<Any>) {
        val fieldsToUpdate = keys.zip(values).toMap()
        userFirebaseDB.updateUserProfileFields(userId, fieldsToUpdate)
    }
}