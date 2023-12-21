package com.example.waygo.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.example.waygo.data.pref.UserPrefs
import com.example.waygo.ui.login.LoginActivity
import com.example.waygo.data.pref.dataStore
import com.example.waygo.data.response.User
import com.example.waygo.databinding.FragmentProfileBinding
import com.example.waygo.helper.Token
import com.example.waygo.helper.VMFactory
import com.example.waygo.helper.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONException

class ProfileFragment : Fragment() {

    companion object {
        private const val TAG = "ProfileFragment"
    }

    private val viewModel by viewModels<ProfileViewModel> {
        VMFactory.getInstance(requireContext(), requireActivity().application)
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivLogout.setOnClickListener{
            Logout()
        }
        getData()
    }


    private fun getData() {
        val pref = UserPrefs.getInstance(requireActivity().dataStore)
        val user = runBlocking { pref.getSession().first() }
        val token = user.accessToken

        try {
            val id = Token.getId(token)
            Log.i(TAG, "token: $token")
            Log.i(TAG, "id: $id")
            setupUser(id)
        } catch (e: JSONException) {
            // Handle the JSONException here, log an error, show a message, or take appropriate action
            Log.e(TAG, "JSONException: ${e.message}")
            // You might want to provide a default value or take another appropriate action
        }
    }

    private  fun setupUser(id :String?)  {
        if (id != null) {
            viewModel.getUser(id).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        showLoading(false)
                        dataUser(result.data.user)
                        Log.i(TAG, "setupDataUser: ${result.data}")
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "setupDataUser: ${result.error}", )
                    }

                    is Result.Loading -> {
                        showLoading(true)
                    }
                }
            }
        }
    }

    private fun dataUser(data : User) {
//        binding.tvUsernameProfil.text = data.username
//        binding.tvEmailUser.text = data.email
        binding.tvDetailName.text= data.name
        binding.tvDetailEmail.text= data.email
//        binding.tvDetailFullname.text = data.name
//        binding.tvDetailAlamat.text= data.name
//        binding.tvDetailTelephone.text = data.name.toInt().toString()

    }

//    private fun Logout() {
//        val alertDialogBuilder = AlertDialog.Builder(requireContext())
//        alertDialogBuilder.setTitle("Konfirmasi Keluar")
//        alertDialogBuilder.setMessage("Anda yakin ingin keluar?")
//        alertDialogBuilder.setPositiveButton("Ya") { _, _ ->
//            viewModel.logout()
//
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            requireActivity().finish()
//        }
//        alertDialogBuilder.setNegativeButton("Tidak") { dialog, _ ->
//            dialog.dismiss()
//        }
//        alertDialogBuilder.create().show()
//    }


    private fun Logout() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        with(dialogBuilder) {
            setTitle("Konfirmasi Keluar")
            setMessage("Anda yakin ingin keluar?")
            setPositiveButton("Ya") { _, _ ->
                viewModel.logout()

                val loginIntent = Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(loginIntent)
                requireActivity().finish()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        val binding = _binding
        if (binding != null) {
            if (isLoading) {
                binding.progressBarProfile.visibility = View.VISIBLE
            } else {
                binding.progressBarProfile.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}