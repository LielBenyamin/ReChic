package com.example.rechic.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.rechic.R
import com.example.rechic.databinding.FragmentAddEditItemBinding
import com.example.rechic.utils.ImageUtils
import com.example.rechic.utils.getNavigationResultLiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.AddEditProductViewModel
import viewmodels.FireBaseState

class AddEditItemFragment :
    BaseFragment<FragmentAddEditItemBinding>(FragmentAddEditItemBinding::inflate) {

    private val addEditViewModel by viewModel<AddEditProductViewModel>()

    val args: AddEditItemFragmentArgs by navArgs()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                addEditViewModel.updateProductImageUri(it)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }

    private fun initViews() {
        val product = args.product
        if (product != null) {
            binding.etProductName.setText(product.name)
            binding.etDescription.setText(product.description)
            binding.etPrice.setText(product.price.toString())
            Glide.with(requireContext())
                .load(product.imgUrl)
                .placeholder(ImageUtils.createShimmerDrawable())
                .into(binding.imgAddPhoto)
        }
        binding.imgAddPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.btnDone.setOnClickListener {
            addEditViewModel.onDoneClicked(
                product,
                binding.etProductName.text.toString(),
                binding.etDescription.text.toString(),
                binding.etPrice.text.toString(),
            )
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                addEditViewModel.productUri.collect { uri ->
                    uri?.let {
                        binding.imgAddPhoto.setImageURI(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                addEditViewModel.upadteState.collect { state ->
                    when (state) {
                        is FireBaseState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnDone.isEnabled = false
                        }

                        is FireBaseState.Success -> {
                            showSnackbar("Item Updated!")
                            findNavController().navigateUp()
                        }

                        is FireBaseState.Error -> {
                            binding.btnDone.isEnabled = true
                            binding.progressBar.visibility = View.GONE
                            showSnackbar(state.message)
                        }
                    }
                }
            }
        }
    }
}