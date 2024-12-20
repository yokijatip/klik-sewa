package com.sinergi5.kliksewa.ui.detail.product

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.adapter.PricePlanAdapter
import com.sinergi5.kliksewa.databinding.ActivityDetailProductBinding
import com.sinergi5.kliksewa.helper.MyHelper
import com.sinergi5.kliksewa.model.PricePlan
import com.sinergi5.kliksewa.model.Product
import com.sinergi5.kliksewa.utils.Resource
import com.sinergi5.kliksewa.utils.ViewModelFactory
import kotlinx.coroutines.launch

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var viewModel: DetailProductViewModel

    private lateinit var productId: String

    private lateinit var pricePlanAdapter: PricePlanAdapter

    private var isDescriptionExpanded = false

    private var quantity = 1
    private var productPrice = 0L
    private var productName = ""

    companion object {
        const val PRODUCT_ID = "product_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
        setupViewModel()
        setupObservers()
        setupPricePlanRecyclerView()
        setupButtonQuantity()
        setupHandleBackButton()
        observerAddItemToCart()

        productId = intent.getStringExtra("product_id") ?: run {
            MyHelper.showMessages("Product ID tidak valid", this)
            finish()
            return
        }

        viewModel.getProductDetail(productId)
    }

    private fun setupButtonQuantity() {
        binding.btnQuantityMin.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }
        binding.btnQuantityPlus.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
        }

        binding.btnAddToCart.setOnClickListener {
            val totalProductPrice = productPrice * quantity
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            MyHelper.showMessages(
                "Added to Cart : $quantity Price: $totalProductPrice User Id: $userId",
                this
            )
            try {
                viewModel.addToCart(productId, productName, quantity, productPrice)
                Log.d("DetailProductActivity", "Product added to cart: $productId User Id: $userId")
            } catch (e: Exception) {
                MyHelper.showErrorSnackBar(binding.root, e.message.toString())
                Log.e("DetailProductActivity", "Error adding to cart: ${e.message}")
            }
        }

    }

    private fun observerAddItemToCart() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToCartStatus.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            //binding.progressBar.visibility = View.VISIBLE
                            //binding.contentLayout.visibility = View.GONE
                            Log.d("DetailProductActivity", "Loading: ${resource.message}")
                        }

                        is Resource.Success -> {
                            Log.d("DetailProductActivity", "Loading: ${resource.message}")
                        }

                        is Resource.Error -> {
                            Log.d("DetailProductActivity", "Loading: ${resource.message}")
                        }
                    }
                }
            }
        }
    }

    private fun setupPricePlanRecyclerView() {
        pricePlanAdapter = PricePlanAdapter { selectedPlan ->
            updateSelectedPricePlan(selectedPlan)
        }

        binding.rvPricePlan.apply {
            adapter = pricePlanAdapter
            layoutManager = LinearLayoutManager(
                this@DetailProductActivity,
                LinearLayoutManager.HORIZONTAL, false
            )
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateSelectedPricePlan(selectedPlan: PricePlan) {
        // Format harga dengan pemisah ribuan
        productPrice = selectedPlan.price
        val formattedPrice = String.format("%,d", selectedPlan.price)

        binding.tvProductPrice.text = "Rp $formattedPrice"
        binding.tvProductPricingPlan.text = when (selectedPlan.type) {
            "hour" -> "/hour"
            "day" -> "/day"
            "week" -> "/week"
            "month" -> "/month"
            else -> "/day"
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.product.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            //binding.progressBar.visibility = View.VISIBLE
                            //binding.contentLayout.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            //binding.progressBar.visibility = View.GONE
                            //binding.contentLayout.visibility = View.VISIBLE
                            resource.data?.let { product ->
                                setupProductDetail(product)
                            }
                        }

                        is Resource.Error -> {
                            //binding.progressBar.visibility = View.GONE
                            MyHelper.showErrorSnackBar(
                                binding.root,
                                resource.message ?: "Terjadi kesalahan"
                            )
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.favoriteStatus.collect { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                // Optional: Show loading indicator
                            }

                            is Resource.Success -> {
                                // Optional: Show success message
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

                launch {
                    viewModel.isProductFavorited.collect { isFavorited ->
                        updateFavoriteIcon(isFavorited)
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon(isFavorited: Boolean) {
        binding.favoriteIcon.setImageResource(
            if (isFavorited) R.drawable.ic_menu_heart_filled
            else R.drawable.ic_menu_heart
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setupProductDetail(product: Product) {
        // Setup Image Slider
        val imageList = ArrayList<SlideModel>()
        product.imageUrl.forEach { imageUrl ->
            imageList.add(SlideModel(imageUrl, scaleType = ScaleTypes.CENTER_CROP))
        }


        binding.ivProductImage.apply {
            setImageList(imageList)
            setSlideAnimation(AnimationTypes.DEPTH_SLIDE)
        }

        // Setup text content
        binding.tvProductName.text = product.name
        productName = product.name
        binding.tvProductPrice.text = "Rp ${product.pricePerDay}"
        binding.tvDescription.text = product.description
        binding.tvProductTotalRatings.text = product.totalRating.toString()

        binding.tvReadMore.setOnClickListener {
            isDescriptionExpanded = !isDescriptionExpanded
            if (isDescriptionExpanded) {
                binding.tvDescription.maxLines = Integer.MAX_VALUE
                binding.tvReadMore.text = getString(R.string.show_less)
            } else {
                binding.tvDescription.maxLines = 3
                binding.tvReadMore.text = getString(R.string.read_more)
            }
        }

        binding.tvDescription.post {
            val lineCount = binding.tvDescription.lineCount
            binding.tvReadMore.visibility = if (lineCount > 2) View.VISIBLE else View.GONE
        }

        // Setup favorite status jika diperlukan
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val isFavorited = currentUserId?.let { userId ->
            product.favoriteBy.contains(userId)
        } ?: false

        viewModel.setInitialFavoriteState(isFavorited)

        binding.favoriteButton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                viewModel.toggleFavorite(productId)
            } else {
                MyHelper.showMessages("Silakan login terlebih dahulu", this)
            }
        }

        val pricePlans = listOfNotNull(
            if (product.pricePerHour > 0) PricePlan("hour", product.pricePerHour) else null,
            if (product.pricePerDay > 0) PricePlan("day", product.pricePerDay) else null,
            if (product.pricePerWeek > 0) PricePlan("week", product.pricePerWeek) else null,
            if (product.pricePerMonth > 0) PricePlan("month", product.pricePerMonth) else null
        )

        // Setup recycler view price plan
        if (pricePlans.isNotEmpty()) {
            binding.rvPricePlan.visibility = View.VISIBLE

            // Set selected berdasarkan prioritas:
            // 1. Jika hanya ada satu price plan, pilih itu
            // 2. Jika ada per day, pilih per day
            // 3. Jika tidak ada keduanya, pilih yang pertama
            val selectedPricePlans = pricePlans.map { pricePlan ->
                when {
                    pricePlans.size == 1 -> pricePlan.copy(isSelected = true)
                    pricePlan.type == "day" && pricePlans.size > 1 -> pricePlan.copy(isSelected = true)
                    else -> pricePlan.copy(isSelected = false)
                }
            }

            pricePlanAdapter.submitList(selectedPricePlans)

            // Update tampilan harga dengan plan yang terpilih
            val defaultPlan =
                selectedPricePlans.find { it.isSelected } ?: selectedPricePlans.first()
            updateSelectedPricePlan(defaultPlan)
        } else {
            binding.rvPricePlan.visibility = View.GONE
            binding.tvProductPrice.text = "Tidak tersedia"
            binding.tvProductPricingPlan.visibility = View.GONE
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[DetailProductViewModel::class.java]
    }

    private fun setupHandleBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback {
            finish()
        }
    }

    private fun setupUI() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}