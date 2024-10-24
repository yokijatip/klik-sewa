package com.sinergi5.kliksewa.ui.cart

import android.graphics.Rect
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.adapter.CartItemAdapter
import com.sinergi5.kliksewa.databinding.ActivityCartBinding
import com.sinergi5.kliksewa.helper.CommonHelper
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.utils.ViewModelFactory

@Suppress("DEPRECATION")
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private val viewModel: CartViewModel by viewModels {
        ViewModelFactory(Repository())
    }

    private lateinit var cartAdapter: CartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCartBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUi()

        // Fetch cart items
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        viewModel.getCartItems()

    }

    private fun setupUi() {
        handleBackButton()
        setupInputUnhidden()
    }

    private fun handleBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupInputUnhidden() {
        // Pastikan window dapat menyesuaikan dengan soft keyboard
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Optional: Jika ingin menambahkan animasi saat keyboard muncul/hilang
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            window.decorView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = window.decorView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard terbuka
                binding.bottomLayout.animate().translationY(-keypadHeight.toFloat()).duration = 200
            } else {
                // Keyboard tertutup
                binding.bottomLayout.animate().translationY(0f).duration = 200
            }
        }
    }



}