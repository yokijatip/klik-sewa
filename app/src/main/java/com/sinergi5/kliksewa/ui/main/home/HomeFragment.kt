package com.sinergi5.kliksewa.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.adapter.CategoryAdapter
import com.sinergi5.kliksewa.adapter.ItemAdapter
import com.sinergi5.kliksewa.data.model.Category
import com.sinergi5.kliksewa.databinding.FragmentHomeBinding
import com.sinergi5.kliksewa.helper.HorizontalSpaceItemDecoration
import com.sinergi5.kliksewa.helper.UiState
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.ui.detail.DetailActivity
import com.sinergi5.kliksewa.utils.ViewModelFactory


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory(Repository())
    }

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var itemRecommendationAdapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewItemRecommendation: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupObservers()

    }

    private fun setupUi() {
        setupTopBarActions()
        imageAutoSlider()
        setupCategory()
        setupItemRecommendations()
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshItems()
        }
    }


    private fun setupTopBarActions() {
        binding.apply {
            btnCart.setOnClickListener {
                Toast.makeText(requireContext(), "Cart button clicked", Toast.LENGTH_SHORT).show()
            }

            btnNotification.setOnClickListener {
                Toast.makeText(requireContext(), "Notification button clicked", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupCategory() {
        val categoryList = listOf(
            Category(name = getString(R.string.camera), icon = R.drawable.ic_camera),
            Category(name = getString(R.string.cookware), icon = R.drawable.ic_cutlery),
            Category(name = getString(R.string.electronic), icon = R.drawable.ic_modern_tv),
            Category(name = getString(R.string.music), icon = R.drawable.ic_headset),
            Category(name = getString(R.string.room), icon = R.drawable.ic_house_rooms),
            Category(name = getString(R.string.vehicle), icon = R.drawable.ic_car),
        )

        categoryAdapter = CategoryAdapter(categoryList) { category ->
            Toast.makeText(requireContext(), category.name, Toast.LENGTH_SHORT).show()
        }

        recyclerView = binding.rvCategory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(16))
        }
    }

    private fun setupItemRecommendations() {
        itemRecommendationAdapter = ItemAdapter { selectedItem ->
            selectedItem.itemId?.let { navigateToDetail(it) }
        }

        recyclerViewItemRecommendation = binding.rvItemRecommendation.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = itemRecommendationAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(16))
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        rvItemRecommendation.visibility = View.GONE
                        errorLayout.visibility = View.GONE
                    }
                }

                is UiState.Success -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        rvItemRecommendation.visibility = View.VISIBLE
                        errorLayout.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                        itemRecommendationAdapter.submitList(state.data)
                    }
                }

                is UiState.Error -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        rvItemRecommendation.visibility = View.GONE
                        errorLayout.visibility = View.VISIBLE
                        swipeRefreshLayout.isRefreshing = false
                        tvError.text = state.message
                        btnRetry.setOnClickListener {
                            viewModel.refreshItems()
                        }
                    }
                }
            }
        }
    }

    private fun imageAutoSlider() {
        val imageList = ArrayList<SlideModel>().apply {
            add(SlideModel(R.drawable.banner_modern_sofa))
            add(SlideModel(R.drawable.banner_black_friday))
        }
        binding.imageAutoSlider.setImageList(imageList, ScaleTypes.FIT)
    }


    private fun navigateToDetail(itemId: String) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("ITEM_ID", itemId)
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}