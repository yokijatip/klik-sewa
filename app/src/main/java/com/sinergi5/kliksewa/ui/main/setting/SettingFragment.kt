package com.sinergi5.kliksewa.ui.main.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sinergi5.kliksewa.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        binding.apply {
            btnLogout.setOnClickListener {
                firebaseAuth.signOut()
                requireActivity().finish()
            }
        }


        return binding.root
    }

}