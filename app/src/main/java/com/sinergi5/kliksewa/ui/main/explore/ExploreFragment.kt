package com.sinergi5.kliksewa.ui.main.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sinergi5.kliksewa.adapter.CategoryItemAdapter
import com.sinergi5.kliksewa.adapter.ItemExploreAdapter
import com.sinergi5.kliksewa.databinding.FragmentExploreBinding
import com.sinergi5.kliksewa.repository.Repository
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
        observeCategories()
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
//            Grid

            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = itemAdapter
        }

        binding.rvCategory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryItemAdapter
        }
    }

    private fun observeCategories() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryItemAdapter.submitList(categories)
        }
        viewModel.itemByCategory.observe(viewLifecycleOwner) {
            itemAdapter.submitList(it)
        }
    }

    private fun handleCategoryItemClick(categoryId: String) {
        // Handle category item click
        Toast.makeText(requireContext(), "Category Item Clicked: $categoryId", Toast.LENGTH_SHORT)
            .show()
    }

    private fun handleItemClick(itemId: String) {
        Toast.makeText(requireContext(), "Item Clicked: $itemId", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}