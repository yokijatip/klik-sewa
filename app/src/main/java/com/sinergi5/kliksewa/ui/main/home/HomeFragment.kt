package com.sinergi5.kliksewa.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sinergi5.kliksewa.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        cart()
        notification()


        return binding.root
    }

    private fun cart() {
        binding.btnCart.setOnClickListener {
            // Handle cart button click
            Toast.makeText(requireContext(), "Cart button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notification() {
        binding.btnNotification.setOnClickListener {
            // Handle notification button click
            Toast.makeText(requireContext(), "Notification button clicked", Toast.LENGTH_SHORT).show()
        }
    }

}