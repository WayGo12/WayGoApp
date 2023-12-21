package com.example.waygo.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.waygo.adapter.LoadingStateAdapter
import com.example.waygo.adapter.VacationAdapter
import com.example.waygo.data.pref.UserPrefs
import com.example.waygo.data.pref.dataStore
import com.example.waygo.data.response.AllTouristSpotsItem
import com.example.waygo.databinding.FragmentHomeBinding
import com.example.waygo.helper.Token
import com.example.waygo.helper.VMFactory
import com.example.waygo.ui.detail.DetailActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import java.util.Timer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? by weak()
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel> {
        VMFactory.getInstance(requireContext(), requireActivity().application)
    }

    private val popularAdapter = VacationAdapter()
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        setupTourism()
        showTourism()
    }

    private fun getData() {
        val pref = UserPrefs.getInstance(requireActivity().dataStore)
        val user = runBlocking { pref.getSession().first() }
        val token = user.accessToken
        binding.tvUsername.text = Token.getName(token)
    }


    private fun setupTourism(){
        val layoutManagerPopular = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvTourism.layoutManager = layoutManagerPopular

        binding.apply {
            rvTourism.layoutManager = layoutManagerPopular
        }
    }


    private fun showTourism() {
        binding.rvTourism.adapter = popularAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                popularAdapter.retry()
            }
        )
        viewModel.getAllTourist().observe(viewLifecycleOwner) {
            popularAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        popularAdapter.setOnClickCallback(object : VacationAdapter.OnItemClickCallback {
            override fun onItemClicked(data: AllTouristSpotsItem) {
                showSelectedItem(data)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        val binding = _binding
        if (binding != null) {
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showSelectedItem(data: AllTouristSpotsItem) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, data.id)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        timer?.cancel()
        timer = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}

fun <T : Any> weak(): ReadWriteProperty<Any?, T?> = WeakReferenceProperty()
class WeakReferenceProperty<T : Any> : ReadWriteProperty<Any?, T?> {
    private var weakReference: WeakReference<T?> = WeakReference(null)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return weakReference.get()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        weakReference = WeakReference(value)
    }
}