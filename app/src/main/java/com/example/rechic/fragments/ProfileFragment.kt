package com.example.rechic.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.rechic.R
import com.example.rechic.activity.AuthActivity
import com.example.rechic.databinding.FragmentProfileBinding
import com.example.rechic.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.ProfileViewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val profileViewModel by activityViewModel<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }

    private fun initViews() {
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_settingsProfileFragment)
        }
        binding.logout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Log Out")
        dialogBuilder.setMessage("Are you sure you want to log out?")

        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            logout()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.create().show()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userProfile.collect { userProfile ->
                    if (userProfile != null) {
                        binding.userName.text = userProfile.userName
                        Glide.with(requireContext())
                            .load(userProfile.profileImageUrl)
                            .placeholder(ImageUtils.createShimmerDrawable())
                            .into(binding.profileImage)
                    } else {
                        binding.profileImage.setImageDrawable(ImageUtils.createShimmerDrawable())
                    }
                }
            }
        }
    }
}