package com.example.smarthealthcare.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smarthealthcare.Fragment.BookmarkFragment
import com.example.smarthealthcare.Fragment.ExploreFragment
import com.example.smarthealthcare.Fragment.HomeFragment
import com.example.smarthealthcare.Fragment.ProfileFragment
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.bottomMenu.setItemSelected(R.id.home)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomMenu.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.explorer -> loadFragment(ExploreFragment())
                R.id.bookmark -> loadFragment(BookmarkFragment())
                R.id.profile -> loadFragment(ProfileFragment())
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.slide_out_left,
                R.anim.fade_in,
                R.anim.slide_out_left
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
