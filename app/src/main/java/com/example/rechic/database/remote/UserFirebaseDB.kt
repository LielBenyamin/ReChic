package com.example.rechic.database.remote

import com.example.rechic.database.local.entities.UserProfileEntity
import com.example.rechic.utils.toUserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserFirebaseDB(private val firestore: FirebaseFirestore) {

    suspend fun checkIfUsernameExists(username: String): Boolean {
        val snapshot = firestore.collection(FirestoreConstants.USERS_COLLECTION)
            .whereEqualTo(UserProfileEntity::userName.name, username)
            .get()
            .await()

        return !snapshot.isEmpty
    }

    suspend fun saveUser(user: UserProfileEntity) {
        firestore.collection(FirestoreConstants.USERS_COLLECTION)
            .document(user.userUid)
            .set(user)
            .await()
    }

    suspend fun getUserProfile(): UserProfileEntity? {
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

    suspend fun fetchAllUsers(): List<UserProfileEntity> {
        return try {
            val snapshot = firestore.collection(FirestoreConstants.USERS_COLLECTION)
                .get()
                .await()
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toUserProfile()
                } catch (e: Exception) {
                    null // Log or handle parsing error for individual documents if needed
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}