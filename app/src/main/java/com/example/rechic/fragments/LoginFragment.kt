package com.example.rechic.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.example.rechic.R
import com.example.rechic.activity.HomeActivity
import com.example.rechic.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodels.FireBaseState
import viewmodels.AuthViewModel


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRegisterText()
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            authViewModel.login(email, password)
        }

        binding.registerLink.setOnClickListener {
            view.findNavController().navigate(R.id.action_login_to_register)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { authState ->
                    when (authState) {
                        is FireBaseState.Success -> {
                            navigateToHomeActivity()
                        }

                        is FireBaseState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showSnackbar(authState.message)
                        }

                        FireBaseState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun initRegisterText() {
        val registerLinkText = getString(R.string.don_t_have_a_username_click_here)
        val spannableString = SpannableString(registerLinkText)
        val startText = 0
        val endText = registerLinkText.indexOf("Click here")
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_dont
                )
            ),
            startText,
            endText,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val startLink = registerLinkText.indexOf("Click here")
        val endLink = registerLinkText.length
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.app_text_color
                )
            ),
            startLink,
            endLink,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            startLink,
            endLink,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.registerLink.text = spannableString
    }
}