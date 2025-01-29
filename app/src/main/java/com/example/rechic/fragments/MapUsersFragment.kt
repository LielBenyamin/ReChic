package com.example.rechic.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rechic.R
import com.example.rechic.database.local.entities.UserProfileEntity
import com.example.rechic.databinding.FragmentLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.MapUsersViewModel


class MapUsersFragment :
    BaseFragment<FragmentLocationBinding>(FragmentLocationBinding::inflate),
    OnMapReadyCallback {

    private val defaultLocation = LatLng(32.0879976, 34.8383596)

    private lateinit var googleMap: GoogleMap
    private val viewModel: MapUsersViewModel by viewModel()
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableMyLocation()
        } else {
            // Handle permission denial gracefully, fallback to default location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val defaultLocation = LatLng(32.0879976, 34.8383596)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        setupObserver()
        setupInfoWindowClickListener()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }


    private fun navigateToUserProfile(user: UserProfileEntity) {
        val action =
            MapUsersFragmentDirections.actionLocationToUserProfileProductFragment(user)
        findNavController().navigate(action)
    }


    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allUsers.collect { users ->
                    googleMap.clear()
                    users.forEach { user ->
                        addMarker(user)
                    }
                }
            }
        }
    }

    private fun addMarker(user: UserProfileEntity) {
        val location = LatLng(user.location.latitude, user.location.longitude)
        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(user.userName)
                .snippet("Tap to view profile")
        )
        marker?.tag = user
    }

    private fun setupInfoWindowClickListener() {
        // Set an info window click listener
        googleMap.setOnInfoWindowClickListener { marker ->
            val user = marker.tag as? UserProfileEntity
            user?.let {
                navigateToUserProfile(it)
            }
        }
        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View? {
                val view = layoutInflater.inflate(R.layout.marker_window, null)
                val title = view.findViewById<TextView>(R.id.title)
                val snippet = view.findViewById<TextView>(R.id.snippet)
                title.text = marker.title
                snippet.text = marker.snippet
                return view
            }
        })
    }

}