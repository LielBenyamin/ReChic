package com.example.rechic.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rechic.R
import com.example.rechic.activity.HomeActivity
import com.example.rechic.databinding.FragmentRegisterBinding
import com.example.rechic.model.Country
import com.example.rechic.utils.CountrySpinnerAdapter
import com.example.rechic.utils.getNavigationResultLiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.AuthViewModel
import viewmodels.CountryViewModel
import viewmodels.FireBaseState

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()
    private val countryViewModel: CountryViewModel by viewModel()


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                authViewModel.updateProfileImageUri(it)
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    when (state) {
                        is FireBaseState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.signUpButton.isEnabled = false
                        }

                        is FireBaseState.Success -> {
                            navigateToHomeActivity()
                        }

                        is FireBaseState.Error -> {
                            binding.signUpButton.isEnabled = true
                            binding.progressBar.visibility = View.GONE
                            showSnackbar(state.message)
                        }
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.profileImageUri.collect { uri ->
                    uri?.let {
                        binding.profileImage.setImageURI(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                countryViewModel.countries.collect { countryList ->
                    val adapter =
                        CountrySpinnerAdapter(requireContext(), listOf(null) + countryList)
                    binding.spinner.adapter = adapter

                    authViewModel.selectedCountry.value?.let { selected ->
                        val index =
                            adapter.getPosition(countryList.find { it.getPrefix() == selected.getPrefix() })
                        if (index >= 0) {
                            binding.spinner.setSelection(index)
                        }
                    }
                }
            }
        }
    }

    private fun initViews() {
        binding.plusImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.signUpButton.setOnClickListener {
            val phonePrefix = if (binding.spinner.selectedItem == null) {
                ""
            } else {
                (binding.spinner.selectedItem as Country).getPrefix()
            }
            authViewModel.registerUser(
                userName = binding.userNameEditText.text.toString(),
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString(),
                confirmPassword = binding.confirmPasswordEditText.text.toString(),
                phoneNumber = binding.phoneEditText.text.toString(),
                phoneNumberPrefix = phonePrefix
            )
        }
        binding.pickLocationButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_mapFragment)
        }
        getNavigationResultLiveData<LatLng>(KEY_LAT_LANG)
            ?.observe(viewLifecycleOwner) { latLng ->
                authViewModel.updateSelectedLocation(latLng)
                binding.addressEditText.setText(resources.getString(R.string.location_is_chosen))
            }

        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    val selectedCountry =
                        parent?.getItemAtPosition(position) as? Country
                    authViewModel.setSelectedCountry(selectedCountry)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    authViewModel.setSelectedCountry(null)
                }
            }
    }


    private fun navigateToHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}