package com.example.waygo.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.example.waygo.R
import com.example.waygo.helper.VMFactory
import com.example.waygo.ui.MainActivity
import com.example.waygo.ui.login.LoginActivity

class SplashScreen : AppCompatActivity() {
    private val viewModel by viewModels<SplashVm> {
        VMFactory.getInstance(this, application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setupSplashScreen()

    }

    private fun setupSplashScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getUser().observe(this){ user ->
                if (!user.isLogin){
                    startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                }
            }
        }, 3000)
    }
}