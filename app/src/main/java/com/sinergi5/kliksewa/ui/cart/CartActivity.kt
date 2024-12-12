package com.sinergi5.kliksewa.ui.cart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.adapter.CartAdapter
import com.sinergi5.kliksewa.databinding.ActivityCartBinding
import com.sinergi5.kliksewa.helper.MyHelper
import com.sinergi5.kliksewa.model.CartItem
import com.sinergi5.kliksewa.utils.Resource
import com.sinergi5.kliksewa.utils.ViewModelFactory
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCartBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
        setupViewModel()
        setupRecyclerView()
        setupObservers()


    }

    private fun setupViewModel() {
        val factory = ViewModelFactory(this@CartActivity)
        viewModel = ViewModelProvider(this, factory)[CartViewModel::class.java]
    }

    private fun setupUI() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter { cartItem, newQuantity ->
            viewModel.updateCartItemQuantity(cartItem.id, newQuantity)
        }

        binding.rvCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(this@CartActivity)

            // Tambahkan divider
            addItemDecoration(
                DividerItemDecoration(
                    this@CartActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun updateTotalPrice(items: List<CartItem>) {
        val subtotal = items.sumOf { it.totalPrice }
        val deliveryFee = 0L // Atau sesuaikan dengan logic delivery fee
        val total = subtotal + deliveryFee

        binding.apply {
            tvSubtotal.text = "Rp ${String.format("%,d", subtotal)}"
            tvDeliveryFee.text = "Rp ${String.format("%,d", deliveryFee)}"
            tvTotal.text = "Rp ${String.format("%,d", total)}"
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe cart items
                launch {
                    viewModel.cartItems.collect { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                // Show loading state
                            }

                            is Resource.Success -> {
                                resource.data?.let { items ->
                                    cartAdapter.submitList(items)
                                    updateTotalPrice(items)
                                }
                            }

                            is Resource.Error -> {
                                MyHelper.showErrorSnackBar(
                                    binding.root,
                                    resource.message ?: "Terjadi kesalahan"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}