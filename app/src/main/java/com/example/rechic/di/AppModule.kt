package com.example.rechic.di

import androidx.room.Room
import com.example.rechic.database.local.AppDatabase
import com.example.rechic.database.remote.ProductFirebaseDB
import com.example.rechic.database.remote.UserFirebaseDB
import com.example.rechic.repository.ImageRepository
import com.example.rechic.repository.ProductRepository
import com.example.rechic.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import viewmodels.AddEditProductViewModel
import viewmodels.AuthViewModel
import viewmodels.HomeActivityViewModel
import viewmodels.HomeFragmentViewModel
import viewmodels.MapUsersViewModel
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
        HomeActivityViewModel(
            productsRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        ProfileViewModel(
            userRepository = get(),
            imageRepository = get(),
            productsRepository = get(),
        )
    }

    viewModel {
        AddEditProductViewModel(
            productRepository = get(),
            imageRepository = get(),
        )
    }

    viewModel {
        HomeFragmentViewModel(
            productsRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        MapUsersViewModel(
            userRepository = get(),
        )
    }

    single {
        ImageRepository(get())
    }

    single {
        UserRepository(
            userFirebaseDB = get(),
            userDao = get(),
        )
    }


    single {
        UserFirebaseDB(
            get()
        )
    }

    single {
        ProductFirebaseDB(
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

    single {
        ProductRepository(
            productFirebaseDB = get(),
            productDao = get(),
            userDao = get(),
        )
    }

    single {
        Room.databaseBuilder(
            context = get(),
            AppDatabase::class.java,
            "product_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().productDao() }
    single { get<AppDatabase>().userProfileDao() }

}