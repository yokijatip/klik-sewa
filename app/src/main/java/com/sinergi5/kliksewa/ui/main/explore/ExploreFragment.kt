package com.sinergi5.kliksewa.ui.main.explore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinergi5.kliksewa.adapter.CategoryItemAdapter
import com.sinergi5.kliksewa.adapter.ItemExploreAdapter
import com.sinergi5.kliksewa.databinding.FragmentExploreBinding
import com.sinergi5.kliksewa.helper.UiState
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.ui.detail.DetailActivity
import com.sinergi5.kliksewa.utils.ViewModelFactory


class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExploreViewModel by viewModels {
        ViewModelFactory(Repository())
    }

    private lateinit var categoryItemAdapter: CategoryItemAdapter
    private lateinit var itemAdapter: ItemExploreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        categoryItemAdapter = CategoryItemAdapter {
            handleCategoryItemClick(it)
        }
        itemAdapter = ItemExploreAdapter {
            // Handle item click
            handleItemClick(it.itemId!!)
        }

        binding.rvItemExplore.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = itemAdapter
        }

        binding.rvCategory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryItemAdapter
        }
    }
    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryItemAdapter.submitList(categories)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        rvItemExplore.visibility = View.GONE
                        errorLayout.visibility = View.GONE
                    }
                }

                is UiState.Success -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        rvItemExplore.visibility = View.VISIBLE
                        errorLayout.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                        itemAdapter.submitList(state.data)
                    }
                }

                is UiState.Error -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        rvItemExplore.visibility = View.GONE
                        errorLayout.visibility = View.VISIBLE
                        swipeRefreshLayout.isRefreshing = false
                        tvError.text = state.message
                        btnRetry.setOnClickListener {
                            viewModel.refresh()
                        }
                    }
                }
            }
        }
    }

    private fun handleCategoryItemClick(categoryId: String) {
        viewModel.fetchItemByCategories(categoryId)
    }

    private fun handleItemClick(itemId: String) {
        navigateToDetail(itemId)
    }

    private fun setupSwipeRefresh() {
        // Mengatur swipe untuk refresh halaman
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
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