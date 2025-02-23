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
import com.example.rechic.database.local.entities.UserProfileEntity
import com.example.rechic.databinding.FragmentSettingsBinding
import com.example.rechic.model.Country
import com.example.rechic.utils.CountrySpinnerAdapter
import com.example.rechic.utils.ImageUtils
import com.example.rechic.utils.getNavigationResultLiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.CountryViewModel
import viewmodels.FireBaseState
import viewmodels.ProfileViewModel

class SettingsProfileFragment :
    BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val profileViewModel by activityViewModel<ProfileViewModel>()
    private val countryViewModel: CountryViewModel by viewModel()

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
            val phonePrefix = if (binding.spinner.selectedItem == null) {
                ""
            } else {
                (binding.spinner.selectedItem as Country).getPrefix()
            }
            profileViewModel.onUpdatedClicked(
                userName = binding.userNameEditText.text.toString(),
                phoneNumber = binding.phoneEditText.text.toString(),
                prefixNumber = phonePrefix,
            )
        }
    }

    private var isCountryReady = false
    private var userProfile: UserProfileEntity? = null



    private fun setAdapter() {
        if (!isCountryReady) {
            return
        }
        val phoneNumber = userProfile?.phoneNumber ?: return
        val prefix = profileViewModel.getPrefixFromPhone(phoneNumber)
        val sufix = profileViewModel.getSuffixFromPhone(phoneNumber)
        val adapter = binding.spinner.adapter as? CountrySpinnerAdapter ?: return
        val countryList = (0 until adapter.count).map { adapter.getItem(it) }
        val selectedIndex = countryList.indexOfFirst { it?.getPrefix() == prefix }
        binding.phoneEditText.setText(sufix)
        if (selectedIndex >= 0) {
            binding.spinner.setSelection(selectedIndex)
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userProfile.collect { userProfile ->
                    if (userProfile != null) {
                        this@SettingsProfileFragment.userProfile = userProfile
                        binding.userNameEditText.setText(userProfile.userName)
                        binding.addressEditText.setText(userProfile.location.toString())
                        setAdapter()
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
                countryViewModel.countries.collect {
                    val adapter = CountrySpinnerAdapter(requireContext(), listOf(null) + it)
                    binding.spinner.adapter = adapter
                    isCountryReady = true
                    setAdapter()
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