package com.example.rechic.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.rechic.R
import com.example.rechic.databinding.FragmentSettingsBinding
import com.example.rechic.utils.ImageUtils
import com.example.rechic.utils.getNavigationResultLiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import viewmodels.FireBaseState
import viewmodels.ProfileViewModel

class SettingsProfileFragment :
    BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val profileViewModel by activityViewModel<ProfileViewModel>()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                profileViewModel.updateProfileImageUri(it)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }

    private fun initViews() {
        binding.editImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.profileImage.setImageDrawable(ImageUtils.createShimmerDrawable())
        binding.pickLocationButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsProfileFragment_to_mapsFragment)
        }
        binding.updateButton.setOnClickListener {
            profileViewModel.onUpdatedClicked(
                binding.userNameEditText.text.toString(),
                binding.phoneEditText.text.toString(),
            )
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userProfile.collect { userProfile ->
                    if (userProfile != null) {
                        binding.userNameEditText.setText(userProfile.userName)
                        binding.addressEditText.setText(userProfile.location.toString())
                        binding.phoneEditText.setText(userProfile.phoneNumber)
                        if (profileViewModel.profileImageUri.value == null) {
                            Glide.with(requireContext())
                                .load(userProfile.profileImageUrl)
                                .placeholder(ImageUtils.createShimmerDrawable())
                                .into(binding.profileImage)
                        }
                    } else {
                        binding.profileImage.setImageDrawable(ImageUtils.createShimmerDrawable())
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.profileImageUri.collect { uri ->
                    uri?.let {
                        binding.profileImage.setImageURI(it)
                    }
                }
            }
        }
        getNavigationResultLiveData<LatLng>(KEY_LAT_LANG)
            ?.observe(viewLifecycleOwner) { latLng ->
                profileViewModel.updateSelectedLocation(latLng)
                binding.addressEditText.setText(resources.getString(R.string.location_is_chosen))
            }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.upadteState.collect { state ->
                    when (state) {
                        is FireBaseState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.updateButton.isEnabled = false
                        }

                        is FireBaseState.Success -> {
                            showSnackbar("Profile Updated")
                            findNavController().navigateUp()
                        }

                        is FireBaseState.Error -> {
                            binding.updateButton.isEnabled = true
                            binding.progressBar.visibility = View.GONE
                            showSnackbar(state.message)
                        }
                    }
                }
            }
        }
    }
}