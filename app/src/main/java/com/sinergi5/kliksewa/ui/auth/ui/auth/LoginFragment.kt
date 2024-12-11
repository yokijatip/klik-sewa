package com.sinergi5.kliksewa.ui.auth.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sinergi5.kliksewa.MainActivity
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.databinding.FragmentLoginBinding
import com.sinergi5.kliksewa.helper.MyHelper
import com.sinergi5.kliksewa.utils.Resource
import com.sinergi5.kliksewa.utils.ViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupViewModel()
        setupAction()
        observerLoginResult()
        login()
        return binding.root
    }

    private fun setupAction() {
        navigateToRegister()
    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if (isInputValid(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.btnLogin.isEnabled = false

        // Add text change listeners to enable/disable button
        binding.edtEmail.addTextChangedListener { validateInput() }
        binding.edtPassword.addTextChangedListener { validateInput() }
    }

    private fun validateInput() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        binding.btnLogin.isEnabled = email.isNotEmpty() && password.isNotEmpty()
    }

    private fun isInputValid(email: String, password: String): Boolean {
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
            binding.edtPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.edtPassword.error = null // Clear error if valid
        }

        return isValid
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun observerLoginResult() {
        viewModel.loginResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
//                    Loading Bar
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
//                    Loading Bar
                    binding.progressBar.visibility = View.GONE
                    navigateToMain()
                    MyHelper.showSuccessSnackBar(
                        binding.root,
                        "Login Success"
                    )
                }

                is Resource.Error -> {
//                    Loading Bar
                    binding.progressBar.visibility = View.GONE
                    MyHelper.showErrorSnackBar(
                        binding.root,
                        resource.message.toString(),
                        onRetry = { login() }
                    )
                }
            }
        }
    }

    private fun setupViewModel() {
        val viewModelFactory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
    }

    private fun navigateToMain() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToRegister() {
        binding.tvSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, RegisterFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}