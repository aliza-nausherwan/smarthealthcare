package com.example.smarthealthcare.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)

        // Apply animations
        binding.logoImg.startAnimation(bounce)
        binding.appNameTv.startAnimation(slideUp)
        binding.appSubTitleTv.startAnimation(fadeIn)
        binding.devTv.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }, 3000) // 3 seconds delay
    }
}
