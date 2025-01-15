package com.example.rechic.di

import com.example.rechic.database.remote.UserFirebaseDB
import com.example.rechic.repository.ImageRepository
import com.example.rechic.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import viewmodels.AuthViewModel
import viewmodels.ProfileViewModel

val appModule = module {

    single {
        FirebaseAuth.getInstance()
    }

    viewModel {
        AuthViewModel(
            firebaseAuth = get(),
            imageRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        ProfileViewModel(
            userRepository = get(),
            imageRepository = get(),
        )
    }

    single {
        ImageRepository(get())
    }

    single {
        UserRepository(get())
    }

    single {
        UserFirebaseDB(
            get()
        )
    }

    single {
        FirebaseAuth.getInstance()
    }

    single {
        FirebaseFirestore.getInstance()
    }

    single {
        FirebaseStorage.getInstance()
    }

    single {
        ImageRepository(get())
    }
}