package com.example.rechic.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.rechic.R
import com.example.rechic.activity.AuthActivity
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.databinding.FragmentProfileBinding
import com.example.rechic.recyclerview.ProductAdapter
import com.example.rechic.utils.ImageUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import viewmodels.ProfileViewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val profileViewModel by activityViewModel<ProfileViewModel>()
    private val productAdapter by lazy {
        ProductAdapter(
            onEditClickedListener = { productEntity ->
                val action =
                    ProfileFragmentDirections.actionProfileToAddEditItemFragment(productEntity.product)
                findNavController().navigate(action)
            },
            onDeleteClickedListener = { productEntity ->
                showDeleteItemDialog(productEntity.product)
            },
        )
    }

    private fun showDeleteItemDialog(productEntity: ProductEntity) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete Item")
        dialogBuilder.setMessage("Are you sure you want to delete \n${productEntity.name}")

        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            profileViewModel.deleteItem(productEntity)
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.create().show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initRecyclerView()
        observe()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = productAdapter
        }
    }

    private fun initViews() {
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_settingsProfileFragment)
        }
        binding.logout.setOnClickListener {
            showLogoutDialog()
        }
        binding.fabAddItem.setOnClickListener {
            findNavController().navigate(R.id.addEditItemFragment)
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
        profileViewModel.onSignOut()
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.allUserProducts.collect { productList ->
                    binding.itemsText.text = getString(R.string.items_text, productList.size)
                    productAdapter.submitData(productList)
                }
            }
        }

    }
}