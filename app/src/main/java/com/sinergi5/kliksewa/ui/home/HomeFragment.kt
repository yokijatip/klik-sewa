package com.sinergi5.kliksewa.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.adapter.ProductAdapter
import com.sinergi5.kliksewa.databinding.FragmentHomeBinding
import com.sinergi5.kliksewa.ui.detail.product.DetailProductActivity
import com.sinergi5.kliksewa.utils.Resource
import com.sinergi5.kliksewa.utils.ViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupImageSlider()
        setupRecyclerViewRecommendProduct()
        observerRecommendProducts()

    }

    private fun setupViewModel() {
        val viewModelFactory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private fun setupImageSlider() {
        val imageList = ArrayList<SlideModel>()

        // Tambahkan gambar-gambar banner Anda
        imageList.add(SlideModel(R.drawable.banner_modern_sofa, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.banner_black_friday, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.banner_klik_sewa, ScaleTypes.CENTER_CROP))
        // Tambahkan banner lainnya sesuai kebutuhan

        binding.imageSlider.setImageList(imageList)
    }

    private fun setupRecyclerViewRecommendProduct() {
        productAdapter = ProductAdapter().apply {
            setCurrentUserId(FirebaseAuth.getInstance().currentUser?.uid)
        }
        binding.rvProductRecommend.apply {
            adapter = productAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        productAdapter.setOnItemClickListener {
//            Nanti di implementasi untuk navigasi ke detail product
            navigateToDetailProduct(it.id)
        }

        productAdapter.setOnFavoriteClickListener { product, position ->
            viewModel.toggleFavorite(product.id)

            FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                productAdapter.updateFavoriteStatus(position, userId)
            }
        }
    }

    private fun observerRecommendProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            // Tampilkan loading jika diperlukan
                            binding.rvProductRecommend.visibility = View.GONE
                            binding.progressBarRecommendedForYou.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.rvProductRecommend.visibility = View.VISIBLE
                            binding.progressBarRecommendedForYou.visibility = View.GONE
                            resource.data?.let { products ->
                                productAdapter.submitList(products)
                            }
                        }

                        is Resource.Error -> {
                            binding.rvProductRecommend.visibility = View.GONE
                            binding.progressBarRecommendedForYou.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun navigateToDetailProduct(productId: String) {
//        Navigate to Detail Product with Intent ID
        val intent = Intent(requireContext(), DetailProductActivity::class.java)
        intent.putExtra(DetailProductActivity.PRODUCT_ID, productId)
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}