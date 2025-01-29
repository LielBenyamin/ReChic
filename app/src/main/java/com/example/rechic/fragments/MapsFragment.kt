package com.example.rechic.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.rechic.R
import com.example.rechic.databinding.FragmentMapsBinding
import com.example.rechic.utils.setNavigationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

const val KEY_LAT_LANG = "lat_lang"

class MapsFragment : BaseFragment<FragmentMapsBinding>(FragmentMapsBinding::inflate),
    OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var selectedLocation: LatLng? = null
    private var marker: Marker? = null
    private val defaultLocation = LatLng(32.0879976, 34.8383596)

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

    private fun initViews() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.confirmLocationButton.setOnClickListener {
            selectedLocation?.let {
                setNavigationResult(KEY_LAT_LANG, it)
            }
            findNavController().popBackStack()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        googleMap.setOnMapClickListener { latLng -> onMapClicked(latLng) }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            // Request location permission
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun onMapClicked(latLng: LatLng) {
        selectedLocation = latLng
        marker?.remove() // Remove the previous marker
        marker =
            googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
    }
}