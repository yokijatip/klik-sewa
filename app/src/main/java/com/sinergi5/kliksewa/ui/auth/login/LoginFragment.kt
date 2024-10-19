package com.sinergi5.kliksewa.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.databinding.FragmentLoginBinding
import com.sinergi5.kliksewa.helper.CommonHelper
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.ui.auth.AuthViewModel
import com.sinergi5.kliksewa.ui.auth.register.RegisterFragment
import com.sinergi5.kliksewa.ui.main.MainActivity
import com.sinergi5.kliksewa.utils.ViewModelFactory


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var viewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        viewModel =
            ViewModelProvider(this, ViewModelFactory(Repository()))[AuthViewModel::class.java]

//        Write code here
        binding.apply {
            tvForgotPassword.setOnClickListener {
                CommonHelper.toast(requireContext(), "Under Construction")
            }

            btnRegister.setOnClickListener {
                navigateToRegister()
            }

            btnLogin.setOnClickListener {
                if (isInputValid()) {
                    val email = edtEmail.text.toString().trim()
                    val password = edtPassword.text.toString().trim()
                    viewModel.login(email, password)
                }
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                CommonHelper.toast(requireContext(), "Login berhasil")
                navigateToMain()
            }.onFailure { exception ->
                CommonHelper.toast(requireContext(), "Login gagal: ${exception.message}")
            }
        }


        return binding.root
    }

    private fun isInputValid(): Boolean {
        binding.apply {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = "Email tidak valid"
                return false
            }

            if (password.isEmpty()) {
                edtPassword.error = "Password tidak boleh kosong"
                return false
            }

            if (password.length < 6) {
                edtPassword.error = "Password minimal 6 karakter"
                return false
            }
        }
        return true
    }

    private fun navigateToRegister() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_auth, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        // Menghapus AuthActivity dari back stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // Menyelesaikan AuthActivity
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}