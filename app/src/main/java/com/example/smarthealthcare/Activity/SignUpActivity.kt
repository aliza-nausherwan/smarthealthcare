package com.example.smarthealthcare.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthealthcare.Model.UserModel
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.ActivitySignupBinding
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val database = FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyAnimations()

        binding.btnSignup.setOnClickListener {
            validateAndSignUp()
        }

        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        binding.tvCreateAccount.startAnimation(fadeIn)
        binding.cardSignup.startAnimation(slideUp)
    }

    private fun validateAndSignUp() {
        val username = binding.etNewUsername.text.toString().trim()
        val email = binding.etNewEmail.text.toString().trim()
        val password = binding.etNewPassword.text.toString().trim()

        if (username.isEmpty()) {
            binding.etNewUsername.error = "Username is required"
            return
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etNewEmail.error = "Valid email is required"
            return
        }
        if (password.length < 6) {
            binding.etNewPassword.error = "Password must be at least 6 characters"
            return
        }

        // Checking if username exists first might be better, but sticking to simple write for now
        val userRef = database.getReference("Users").child(username)
        val user = UserModel(username, email, password, "")

        userRef.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
