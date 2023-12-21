package com.example.waygo.ui.rundown

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.waygo.databinding.ActivityGenerateRundownBinding
import com.example.waygo.helper.Result
import com.example.waygo.helper.VMFactory

class GenerateRundown : Fragment() {

    private val viewModel by viewModels<GenerateRundownVM> {
        VMFactory.getInstance(requireContext(), requireActivity().application)
    }
    private lateinit var binding: ActivityGenerateRundownBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityGenerateRundownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRundown.setOnClickListener { onClickRundown() }
        }
    }

    private fun onClickRundown() {
        val user_id = binding.edUserid.text.toString()
        val region = binding.edRegion.text.toString()

        if (user_id.isNotEmpty()) {
            observeRegistrationResult(user_id, region)
        } else {
            Toast.makeText(requireContext(), "isi region", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeRegistrationResult(user_id: String, region: String) {
        viewModel.rundownUser(user_id, region).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Rundown Berhasil Dibuat", Toast.LENGTH_SHORT).show()

                    // Start ResultGenerateRundown activity
//                    val intent = Intent(requireContext(), CustomRundown::class.java)
//                    startActivity(intent)
//                    requireActivity().finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.error.toString(), Toast.LENGTH_SHORT).show()
                    Log.i("Rundown", "observeRundownResult: ${result.error}")
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        requireActivity().runOnUiThread {
            if (isLoading) {
                binding.progressBarRundown.visibility = View.VISIBLE
            } else {
                binding.progressBarRundown.visibility = View.GONE
            }
        }
    }


}
