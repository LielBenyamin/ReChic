package com.example.rechic.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rechic.databinding.FragmentHomeBinding
import com.example.rechic.recyclerview.ProductAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.HomeFragmentViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel by viewModel<HomeFragmentViewModel>()
    private val productAdapter by lazy {
        ProductAdapter(
            onCardClickedListener = { productEntityWithUserProfile ->
                val action = HomeFragmentDirections.actionHomeToProductItemFragment(
                    productEntityWithUserProfile
                )
                findNavController().navigate(action)
            }
        )
    }

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
                homeViewModel.allProducts.collect { productList ->
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