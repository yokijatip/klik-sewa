package com.sinergi5.kliksewa.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.databinding.ActivitySplashBinding
import com.sinergi5.kliksewa.ui.auth.OnBoardingActivity
import com.sinergi5.kliksewa.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            delay(1000)
            // Pengecekan apakah user sudah login atau belum
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // Jika user sudah login, arahkan ke halaman utama
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                // Jika user belum login, arahkan ke halaman OnBoarding
                startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
            }

            finish() // Tutup SplashActivity
        }
    }

}