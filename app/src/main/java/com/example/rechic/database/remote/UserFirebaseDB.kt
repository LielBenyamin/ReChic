package com.example.rechic.database.remote

import com.example.rechic.model.UserProfile
import com.example.rechic.utils.toUserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserFirebaseDB(private val firestore: FirebaseFirestore) {

    suspend fun checkIfUsernameExists(username: String): Boolean {
        val snapshot = firestore.collection(FirestoreConstants.USERS_COLLECTION)
            .whereEqualTo(UserProfile::userName.name, username)
            .get()
            .await()

        return !snapshot.isEmpty
    }

    suspend fun saveUser(userId: String, user: UserProfile) {
        firestore.collection(FirestoreConstants.USERS_COLLECTION)
            .document(userId)
            .set(user)
            .await()
    }

    suspend fun getUserProfile(): UserProfile? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        val snapshot = firestore.collection(FirestoreConstants.USERS_COLLECTION)
            .document(uid)
            .get()
            .await()

        return if (snapshot.exists()) {
            snapshot.toUserProfile()
        } else {
            null
        }
    }

    suspend fun updateUserProfileFields(userId: String, fields: Map<String, Any>) {
        try {
            val userRef = firestore.collection(FirestoreConstants.USERS_COLLECTION).document(userId)
            userRef.update(fields).await()
        } catch (e: Exception) {
        }
    }
}