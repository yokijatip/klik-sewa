package com.sinergi5.kliksewa.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.databinding.ActivityMainBinding
import com.sinergi5.kliksewa.ui.main.explore.ExploreFragment
import com.sinergi5.kliksewa.ui.main.favorite.FavoriteFragment
import com.sinergi5.kliksewa.ui.main.home.HomeFragment
import com.sinergi5.kliksewa.ui.main.setting.SettingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        replaceFragment(HomeFragment())

        binding.apply {
            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> replaceFragment(HomeFragment())
                    R.id.explore -> replaceFragment(ExploreFragment())
                    R.id.favorite -> replaceFragment((FavoriteFragment()))
                    R.id.setting -> replaceFragment(SettingFragment())
                    else -> {

                    }
                }
                true
            }
        }


    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_main, fragment)
        fragmentTransaction.commit()

    }
}