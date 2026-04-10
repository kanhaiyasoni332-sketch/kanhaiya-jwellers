package com.kanhaiyajewellers.creditmanager.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.kanhaiyajewellers.creditmanager.databinding.ActivitySplashBinding
import com.kanhaiyajewellers.creditmanager.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigate to MainActivity after 2.5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            // Slide-in animation (left → right)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2500L)
    }
}
