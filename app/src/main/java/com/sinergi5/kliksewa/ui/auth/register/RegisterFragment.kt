package com.sinergi5.kliksewa.ui.auth.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.databinding.FragmentRegisterBinding
import com.sinergi5.kliksewa.helper.CommonHelper
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.ui.auth.AuthViewModel
import com.sinergi5.kliksewa.ui.auth.login.LoginFragment
import com.sinergi5.kliksewa.utils.ViewModelFactory


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var repository: Repository
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        viewModel =
            ViewModelProvider(this, ViewModelFactory(Repository()))[AuthViewModel::class.java]

//      Write Code Here
        binding.apply {
            btnBack.setOnClickListener {
                navigateToLogin()
            }

            btnRegister.setOnClickListener {
                if (isInputValid()) {
                    val email = edtEmail.text.toString().trim()
                    val password = edtPassword.text.toString().trim()
                    val name = edtName.text.toString().trim()
                    val phone = edtPhone.text.toString().trim()
                    viewModel.register(email, password, name, phone)
                }
            }
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                CommonHelper.toast(requireContext(), "Register berhasil")
                navigateToLoginIfSuccess()
            }.onFailure { exception ->
                CommonHelper.toast(requireContext(), "Register gagal: ${exception.message}")
            }
        }

        return binding.root
    }

    private fun isInputValid(): Boolean {
        binding.apply {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val name = edtName.text.toString().trim()
            val phone = edtPhone.text.toString().trim()

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

            if (phone.isEmpty()) {
                edtPhone.error = "Nomor telepon tidak boleh kosong"
                return false
            }

            if (name.isEmpty()) {
                edtName.error = "Nama tidak boleh kosong"
                return false
            }
        }
        return true
    }

    private fun navigateToLogin() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_auth, LoginFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToLoginIfSuccess() {
        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_auth, LoginFragment())
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}