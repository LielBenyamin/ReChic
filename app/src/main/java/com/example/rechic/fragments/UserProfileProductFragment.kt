package com.example.rechic.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rechic.databinding.FragmentHomeBinding
import com.example.rechic.databinding.FragmentUserProfleBinding
import com.example.rechic.recyclerview.ProductAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.HomeFragmentViewModel

class UserProfileProductFragment :
    BaseFragment<FragmentUserProfleBinding>(FragmentUserProfleBinding::inflate) {

    private val homeViewModel by viewModel<HomeFragmentViewModel>()
    private val productAdapter by lazy { ProductAdapter() }

    val args: UserProfileProductFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViews()
        observe()
    }

    private fun initViews() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.syncData()
        }
        binding.userName.text = args.user.userName
        binding.contactButton.setOnClickListener {
            openDialerWithPhoneNumber(args.user.phoneNumber)
        }
    }

    private fun openDialerWithPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber") // Use "tel:" URI scheme
        }
        startActivity(dialIntent)
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = productAdapter
        }
    }


    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.getUserProducts(args.user.userUid).collect { productList ->
                    productAdapter.submitData(productList)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.syncState.collect { isSync ->
                    binding.swipeRefreshLayout.isRefreshing = isSync
                }
            }
        }
    }
}