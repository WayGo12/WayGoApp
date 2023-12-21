package com.example.waygo.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.waygo.R
import com.example.waygo.data.response.TouristSpot
import com.example.waygo.databinding.ActivityDetailBinding
import com.example.waygo.helper.VMFactory
import com.example.waygo.helper.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<DetailViewModel> {
        VMFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}