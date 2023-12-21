package com.example.waygo.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import com.example.waygo.ui.MainActivity
import com.example.waygo.R
import com.example.waygo.data.pref.UserModel
import com.example.waygo.databinding.ActivityWelcomeBinding
import com.example.waygo.helper.VMFactory
import com.example.waygo.helper.Result

class WelcomeActivity : AppCompatActivity() {

    private val viewModel by viewModels<WelcomeViewModel> {
        VMFactory.getInstance(this)
    }

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var adapter: WelcomeAdapter
    private lateinit var dots: Array<TextView?>
    private lateinit var layouts: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSkip.setOnClickListener { onClickSkip() }

        setupLayout()
        setupViewPager()
        setupLogin()

    }

    private fun setupLogin() {
        val email = intent.getStringExtra(EXTRA_EMAIL)
        val password = intent.getStringExtra(EXTRA_PASSWORD)

        if (email != null && password != null){
            viewModel.login(email, password).observe(this){ result ->
                when(result){
                    is Result.Loading -> {
                        Log.d(TAG, "setupLogin: Loading")
                    }
                    is Result.Success -> {
                        Log.d(TAG, "setupLogin: Success")
                        val token = result.data.accessToken
                        val user = UserModel(email, token, true)
                        viewModel.saveSession(user)
                        VMFactory.clearInstance()
                    }
                    is Result.Error -> {
                        Log.d(TAG, "setupLogin: Error")
                    }
                }
            }
        }
    }

    private fun setupLayout() {
        layouts = intArrayOf(
            R.layout.onboarding_screen,
        )
    }

    private fun setupViewPager() {
        adapter = WelcomeAdapter(layouts, this)
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == layouts.size - 1){
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnSkip.visibility = View.VISIBLE
                }
                setDots(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        setDots(0)
    }

    private fun onClickSkip() {
        startActivity(Intent((this@WelcomeActivity), MainActivity::class.java))
        finish()
    }


    @Suppress("DEPRECATION")
    private fun setDots(page: Int) {
        binding.indicator.removeAllViews()
        dots = arrayOfNulls(layouts.size)

        for (i in dots.indices){
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(getColor(R.color.light_gray))
            binding.indicator.addView(dots[i])
        }

        if (dots.isNotEmpty()){
            dots[page]?.setTextColor(getColor(R.color.primary))
        }
    }

    companion object{
        const val TAG = "WelcomeActivity"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PASSWORD = "extra_password"
    }

}