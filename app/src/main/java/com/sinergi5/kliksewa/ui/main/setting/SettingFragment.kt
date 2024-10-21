package com.sinergi5.kliksewa.ui.main.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.adapter.SettingAdapter
import com.sinergi5.kliksewa.data.model.Setting
import com.sinergi5.kliksewa.databinding.FragmentSettingBinding
import com.sinergi5.kliksewa.helper.HorizontalSpaceItemDecoration

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var settingAdapter: SettingAdapter
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        setupListSetting()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        recyclerView = binding.rvSettings
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = settingAdapter

        binding.apply {



        }
//        firebaseAuth.signOut()
//        requireActivity().finish()


        return binding.root
    }

    private fun setupListSetting() {
        val settingList = listOf(
            Setting(id = 1, title = getString(R.string.account), icon = R.drawable.ic_people_tag),
            Setting(id = 2, title = getString(R.string.notification), icon = R.drawable.ic_bell),
            Setting(id = 3, title = getString(R.string.language), icon = R.drawable.ic_language),
            Setting(id = 4, title = getString(R.string.history), icon = R.drawable.ic_clock_rotate_right),
            Setting(id = 5, title = getString(R.string.rate), icon = R.drawable.ic_thumbs_up),
            Setting(id = 6, title = getString(R.string.help), icon = R.drawable.ic_headset_help),
            Setting(id = 7, title = getString(R.string.about), icon = R.drawable.ic_info_circle),
            Setting(id = 8, title = getString(R.string.logout), icon = R.drawable.ic_log_out)
        )

        settingAdapter = SettingAdapter(settingList) {
            Toast.makeText(requireContext(), it.title, Toast.LENGTH_SHORT).show()
            when(it.id) {
                8 -> {
                    firebaseAuth.signOut()
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}