package com.example.rechic.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rechic.R
import com.example.rechic.databinding.FragmentAddEditItemBinding
import com.example.rechic.databinding.FragmentProductDetailBinding
import com.example.rechic.utils.ImageUtils
import com.example.rechic.utils.getNavigationResultLiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.AddEditProductViewModel
import viewmodels.FireBaseState

class ProductItemFragment :
    BaseFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate) {

    val args: ProductItemFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        val product = args.productWrapper.product
        val userProfile = args.productWrapper.userProfile
        binding.userName.text = userProfile.userName
        Glide.with(requireContext())
            .load(product.imgUrl)
            .placeholder(ImageUtils.createShimmerDrawable())
            .apply(RequestOptions().fitCenter())
            .into(binding.imgAddPhoto)
        binding.productName.text = product.name
        binding.productDescription.text = product.description
        binding.productPrice.text = getString(R.string.price_text) + product.price.toString()
//        binding.contactButton.setOnClickListener {
//            openSmsWithContactInfo(
//                userProfile.phoneNumber,
//                "Hello, I am interested in your product: ${product.name}."
//            )
//        }

        binding.contactButton.setOnClickListener {
            openEmailWithContactInfo(
                userProfile.email, "Interest in your product: ${product.name}",
                "Hello, I am interested in your product: ${product.name}."
            )
        }

    }

    private fun openEmailWithContactInfo(email: String, subject: String, body: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email") // "mailto:" URI scheme to send email
            putExtra(Intent.EXTRA_SUBJECT, subject) // Set the subject
            putExtra(Intent.EXTRA_TEXT, body) // Set the body text
        }

        if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(emailIntent) // Launch email app
        } else {
            Toast.makeText(requireContext(), "No email app available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSmsWithContactInfo(phoneNumber: String, message: String) {
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber") // Use sms URI scheme
            putExtra("sms_body", message) // Set the message body
        }
        if (smsIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(smsIntent)
        } else {
            Toast.makeText(requireContext(), "No SMS app available", Toast.LENGTH_SHORT).show()
        }
    }

}