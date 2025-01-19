package com.example.rechic.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.rechic.R
import com.example.rechic.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.HomeActivityViewModel

class HomeActivity : AppCompatActivity() {

    lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeActivityViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragments =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = fragments.navController
        binding.bottomNav.setupWithNavController(navController)
        bottomNavItemChangeListener(binding.bottomNav)
        homeViewModel.syncData()
    }

    private fun bottomNavItemChangeListener(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            if (item.itemId != navView.selectedItemId) {
                navController.popBackStack(item.itemId, inclusive = true, saveState = false)
                navController.navigate(item.itemId)
            } else {
                navController.popBackStack(item.itemId, inclusive = false)
            }
            true
        }

    }
}