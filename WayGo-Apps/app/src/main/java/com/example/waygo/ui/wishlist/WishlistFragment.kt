package com.example.waygo.ui.wishlist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.waygo.R
import com.example.waygo.adapter.VacationAdapter
import com.example.waygo.data.response.AllTouristSpotsItem
import com.example.waygo.data.response.VocationEntity
import com.example.waygo.databinding.FragmentWishlistBinding
import com.example.waygo.ui.detail.DetailActivity


class WishlistFragment : Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private val popularAdapter = VacationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the title in the action bar
        requireActivity().title = getString(R.string.wishlist)

        val favoriteViewModel = obtainViewModel(this)
        favoriteViewModel.getAllWishlistData().observe(viewLifecycleOwner) {
            setWishlistData(it)
        }

        favoriteViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun setWishlistData(userEntities: List<VocationEntity>) {
        val items = ArrayList<VocationEntity>()
        userEntities.map {
            val item = VocationEntity(
                name = it.name,
                imgUrl = it.imgUrl,
                category = it.category
            )
            items.add(item)
        }
        val adapter = WishlistAdapater(items)
        binding.rvWishlist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWishlist.setHasFixedSize(true)
        binding.rvWishlist.adapter = adapter

        adapter.setOnItemClickCallback(object : WishlistAdapater.OnItemClickCallback {
            override fun onItemClicked(data: VocationEntity) {
                startActivity(
                    Intent(requireContext(), DetailActivity::class.java)
                        .putExtra(DetailActivity.EXTRA_NAME, data.name)
                        .putExtra(DetailActivity.EXTRA_CATEGORY, data.category)
                        .putExtra(DetailActivity.EXTRA_IMAGE, data.imgUrl)
                )
            }
        })
        popularAdapter.setOnClickCallback(object : VacationAdapter.OnItemClickCallback {
            override fun onItemClicked(data: AllTouristSpotsItem) {
                showSelectedItem(data)
            }
        })
    }
    private fun showSelectedItem(data: AllTouristSpotsItem) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, data.id)
        startActivity(intent)
    }

    private fun obtainViewModel(fragment: Fragment): WishlistViewModel {
        val factory = WishlistViewModel.ViewModelFactory.getInstance(fragment.requireActivity().application)
        return ViewModelProvider(fragment, factory)[WishlistViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}