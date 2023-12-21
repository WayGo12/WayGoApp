package com.example.waygo.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.waygo.R
import com.example.waygo.data.response.TouristSpot
import com.example.waygo.data.response.VocationEntity
import com.example.waygo.databinding.ActivityDetailBinding
import com.example.waygo.helper.VMFactory
import com.example.waygo.helper.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var isFavorite: Boolean = false

    private val viewModel by viewModels<DetailViewModel> {
        VMFactory.getInstance(this, application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(EXTRA_NAME) ?: ""
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE) ?: ""

        val bundle = Bundle()
        bundle.putString(EXTRA_NAME, name)
        bundle.putString(EXTRA_CATEGORY, category)
        bundle.putString(EXTRA_IMAGE, imageUrl)

        configToolbar()
        val id = intent.getStringExtra(EXTRA_DATA)
        getData(id!!)

    }

    private fun configToolbar() {
        val toolbar = binding.appBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle("")
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_favorite -> {
                    Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun getData(id: String) {

        viewModel.getTouristById(id).observe(this){ result ->
            when(result){
                is Result.Success -> {
                    showLoading(false)
                    setupData(result.data.touristSpot )
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "detailError : ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    showLoading(true)
                }
            }
        }
    }


    private fun setupData(data: TouristSpot) {
        Glide.with(this)
            .load(data.imageUrl)
            .into(binding.imgDetail)
        binding.apply {
            tvTitle.text = data.name
            tvKategori.text = data.category
            tvIngredients.text = data.openingHours
            tvDetailLokasi.text = data.location
        }
        viewModel.getDataByUsername(name = data.name).observe(this) {
            isFavorite = it.isNotEmpty()
            val favoriteUser = VocationEntity(data.name, data.imageUrl, data.category)
            if (it.isEmpty()) {
                binding.btnWishlist.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.btnWishlist.context,
                        R.drawable.baseline_favorite_24
                    )
                )
                binding.btnWishlist.contentDescription = getString(R.string.wishlist_added)
            } else {
                binding.btnWishlist.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.btnWishlist.context,
                        R.drawable.baseline_favorite_24_dark
                    )
                )
                binding.btnWishlist.contentDescription = getString(R.string.wishlist_removed)
            }

            binding.btnWishlist.setOnClickListener {
                if (isFavorite) {
                    viewModel.delete(favoriteUser)
                    Toast.makeText(this, R.string.wishlist_removed, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.insert(favoriteUser)
                    Toast.makeText(this, R.string.wishlist_added, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_IMAGE = "extra_image"
    }
}