package com.sinergi5.kliksewa.ui.auth.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sinergi5.kliksewa.databinding.FragmentRegisterBinding
import com.sinergi5.kliksewa.helper.MyHelper
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.utils.Resource
import com.sinergi5.kliksewa.utils.ViewModelFactory


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    companion object {
        fun newInstance() = RegisterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupViewModel()
        setupAction()
        observerRegisterResult()
        register()
        return binding.root
    }

    private fun setupAction() {
        navigateToLogin()
        handleBackButton()
    }

    private fun setupViewModel() {
        val repository = Repository(requireActivity())
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
    }

    private fun observerRegisterResult() {
        viewModel.registerResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
//                    Loading Bar
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
//                    Loading Bar
                    binding.progressBar.visibility = View.GONE
                    MyHelper.showSuccessSnackBar(
                        binding.root,
                        "Register Success"
                    )
                    parentFragmentManager.popBackStack()
                }

                is Resource.Error -> {
//                    Loading Bar
                    binding.progressBar.visibility = View.GONE
                    MyHelper.showErrorSnackBar(
                        binding.root,
                        resource.message.toString(),
                        onRetry = { register() }
                    )
                }
            }
        }
    }

    private fun register() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val confirmationPassword = binding.edtConfirmationPassword.text.toString().trim()
            val username = binding.edtUsername.text.toString().trim()
            val phoneNumber = binding.edtPhone.text.toString().trim()
            if (isInputValid(email, password, confirmationPassword)) {
                viewModel.register(username, phoneNumber, email, password)
            }
        }

        binding.btnSignUp.isEnabled = false

        // Add text change listeners to enable/disable button
        binding.edtEmail.addTextChangedListener { validateInput() }
        binding.edtPassword.addTextChangedListener { validateInput() }
    }

    private fun validateInput() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        binding.btnSignUp.isEnabled =
            email.isNotEmpty() && password.isNotEmpty()
    }

    private fun isInputValid(
        email: String,
        password: String,
        confirmationPassword: String
    ): Boolean {
        var isValid = true

//        Validate Email Input
        if (!isEmailValid(email)) {
            binding.edtEmail.error = "Invalid email format"
            isValid = false
        } else {
            binding.edtEmail.error = null // Clear error if valid
        }

        // Validate password length
        if (password.length < 6) {
            binding.edtPasswordLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.edtPasswordLayout.error = null // Clear error if valid
        }

        if (confirmationPassword.length < 6) {
            binding.edtConfirmationPasswordLayout.error = "Confirm password is required"
            isValid = false
        } else {
            binding.edtConfirmationPasswordLayout.error = null // Clear error if valid
        }

        if (password != confirmationPassword) {
            binding.edtConfirmationPasswordLayout.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun navigateToLogin() {
        binding.tvSignIn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun handleBackButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                parentFragmentManager.popBackStack()
            }
            navigateToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}