package com.example.rechic.repository

import android.net.Uri
import com.example.rechic.database.remote.FirestoreConstants
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ImageRepository(private val storage: FirebaseStorage) {


    suspend fun uploadProfileImage(userId: String, profileImageUri: Uri): String? {
        return try {
            val imageRef =
                storage.reference.child("${FirestoreConstants.PROFILE_IMAGES_PATH}/$userId.jpg")
            imageRef.putFile(profileImageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}