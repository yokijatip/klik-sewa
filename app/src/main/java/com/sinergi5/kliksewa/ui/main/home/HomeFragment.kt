package com.sinergi5.kliksewa.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.utils.ViewModelFactory


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

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
        homeViewModel =
            ViewModelProvider(this, ViewModelFactory(Repository()))[HomeViewModel::class.java]
        cart()
        notification()
        imageAutoSlider()
        setupCategory()

//        Category Setup
        recyclerView = binding.rvCategory
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = categoryAdapter
        recyclerView.addItemDecoration(HorizontalSpaceItemDecoration(16))
        recyclerView.adapter = categoryAdapter

//        ItemRecommendation Setup
        itemRecommendationAdapter = ItemAdapter { selectedItem ->
            // Handle item click
            Toast.makeText(requireContext(), selectedItem.name, Toast.LENGTH_SHORT).show()
        }
        recyclerViewItemRecommendation = binding.rvItemRecommendation
        recyclerViewItemRecommendation.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewItemRecommendation.adapter = itemRecommendationAdapter
        recyclerViewItemRecommendation.addItemDecoration(HorizontalSpaceItemDecoration(16))
        recyclerViewItemRecommendation.adapter = itemRecommendationAdapter



        homeViewModel.items.observe(viewLifecycleOwner) { items ->
            itemRecommendationAdapter.submitList(items)
        }



        return binding.root
    }

    private fun cart() {
        binding.btnCart.setOnClickListener {
            // Handle cart button click
            Toast.makeText(requireContext(), "Cart button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notification() {
        binding.btnNotification.setOnClickListener {
            // Handle notification button click
            Toast.makeText(requireContext(), "Notification button clicked", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun imageAutoSlider() {
        val imageList = ArrayList<SlideModel>()
        binding.apply {
            imageList.add(SlideModel(R.drawable.banner_modern_sofa))
            imageList.add(SlideModel(R.drawable.banner_black_friday))

            imageAutoSlider.setImageList(imageList, ScaleTypes.FIT)
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
            // Handle category click
            Toast.makeText(requireContext(), category.name, Toast.LENGTH_SHORT).show()
        }

    }

}