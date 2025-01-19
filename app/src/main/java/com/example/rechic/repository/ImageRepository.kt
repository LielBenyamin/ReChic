package com.example.rechic.repository

import android.net.Uri
import com.example.rechic.database.remote.FirestoreConstants
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

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

    suspend fun uploadProductImage(userId: String, profileImageUri: Uri): String? {
        return try {
            val imageRef =
                storage.reference.child("${FirestoreConstants.PRODUCTS_IMAGES_PATH}/$userId/${generateUUID()}.jpg")
            imageRef.putFile(profileImageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}