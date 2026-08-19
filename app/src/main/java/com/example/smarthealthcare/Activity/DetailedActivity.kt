package com.example.smarthealthcare.Activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.smarthealthcare.Model.DoctorsModel
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.ActivityDetailedBinding

class DetailedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailedBinding
    private lateinit var item: DoctorsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
        applyAnimations()
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        binding.doctorImg.startAnimation(fadeIn)
        binding.backBtn.startAnimation(scaleUp)
        binding.titleTxt.startAnimation(slideUp)
        binding.specialtyTxt.startAnimation(slideUp)
        binding.bookBtn.startAnimation(slideUp)
    }

    private fun getBundle() {
        item = intent.getParcelableExtra("object")!!

        binding.titleTxt.text = item.Name
        binding.specialtyTxt.text = item.Special
        binding.descriptionTxt.text = item.Biography
        binding.experienceTxt.text = "${item.Experience} years"
        binding.ratingTxt.text = item.Rating.toString()
        binding.costDetailTxt.text = "Rs. ${item.Cost}"
        binding.addressTxt.text = item.Address
        binding.timeTxt.text = item.Time

        var imageUrl = item.Image
        // Fix for ImgBB viewer links: https://ibb.co/Vp0bxtrH -> https://i.ibb.co/Vp0bxtrH/image.png
        if (imageUrl.contains("ibb.co") && !imageUrl.contains("i.ibb.co")) {
            val code = imageUrl.substringAfter("ibb.co/")
            imageUrl = "https://i.ibb.co/$code/image.png"
        }

        Glide.with(this)
            .load(imageUrl)
            .into(binding.doctorImg)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.bookBtn.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("doctor", item)
            startActivity(intent)
        }
    }
}
