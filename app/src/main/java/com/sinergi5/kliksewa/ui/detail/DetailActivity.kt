package com.sinergi5.kliksewa.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.data.model.Item
import com.sinergi5.kliksewa.databinding.ActivityDetailBinding
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.utils.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backButtonHandler()
        viewModel =
            ViewModelProvider(this, ViewModelFactory(Repository()))[DetailViewModel::class.java]

        val itemId = intent.getStringExtra("ITEM_ID")

        viewModel.itemDetail.observe(this) { result ->
            result.onSuccess {
                bindDataToUi(it)
            }.onFailure {
                showError(it.message)
            }
        }

        if (itemId != null) {
            viewModel.fetchItemDetails(itemId)
        }
    }

    private fun backButtonHandler() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun bindDataToUi(item: Item) {
        binding.apply {
            tvItemName.text = item.name
            tvItemDescription.text = item.description
            tvItemPrice.text = "Rp. ${item.priceDay.toString()} / Per Day"
            tvItemType.text = item.type

            val imageList = ArrayList<SlideModel>()
            item.imageUrls?.forEach {
                imageList.add(SlideModel(it, ScaleTypes.CENTER_CROP))
            }

            imageAutoSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
        }
    }

    private fun showError(message: String?) {
        Toast.makeText(this, message ?: "Error loading item details", Toast.LENGTH_SHORT).show()
    }
}