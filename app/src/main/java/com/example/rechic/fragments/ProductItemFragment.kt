package com.example.rechic.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rechic.R
import com.example.rechic.databinding.FragmentProductDetailBinding
import com.example.rechic.utils.ImageUtils

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
        binding.contactButton.setOnClickListener {
            openDialerWithPhoneNumber(userProfile.phoneNumber)
        }
    }

    private fun openDialerWithPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber") // Use "tel:" URI scheme
        }
        startActivity(dialIntent)
    }
}