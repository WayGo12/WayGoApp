package com.example.waygo.ui.customRundown

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.waygo.adapter.LoadingStateAdapter
import com.example.waygo.adapter.RundownAdapter
import com.example.waygo.databinding.ActivityCustomRundownBinding
import com.example.waygo.helper.VMFactory
import java.lang.ref.WeakReference
import java.util.Timer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CustomRundown :  Fragment() {

    private var _binding: ActivityCustomRundownBinding? by weak()
    private val binding get() = _binding!!

    private val viewModel by viewModels<CustomRundownVM> {
        VMFactory.getInstance(requireContext(), requireActivity().application)
    }

    private val rundownAdapter = RundownAdapter()
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCustomRundownBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        getData()
        setupTourism()
        showTourism()
    }

//    private fun getData() {
//        val pref = UserPrefs.getInstance(requireActivity().dataStore)
//        val user = runBlocking { pref.getSession().first() }
//        val token = user.accessToken
//        binding.tvUsername.text = Token.getName(token)
//    }


    private fun setupTourism(){
        val layoutManagerPopular = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvGenerate.layoutManager = layoutManagerPopular

        binding.apply {
            rvGenerate.layoutManager = layoutManagerPopular
        }
    }


    private fun showTourism() {
        binding.rvGenerate.adapter = rundownAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                rundownAdapter.retry()
            }
        )
        viewModel.getAllRundown().observe(viewLifecycleOwner) {
            rundownAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
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