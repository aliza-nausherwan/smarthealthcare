package com.example.smarthealthcare.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthealthcare.Model.UserModel
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val database = FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyAnimations()

        binding.btnLogin.setOnClickListener {
            val usernameInput = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (usernameInput.isEmpty()) {
                binding.etUsername.error = "Username is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password is required"
                return@setOnClickListener
            }

            val userRef = database.getReference("Users").child(usernameInput)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(UserModel::class.java)
                        if (user != null && user.password == password) {
                            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                // Save all retrieved information to local storage
                                putString("USER_NAME", user.username)
                                putString("USER_EMAIL", user.email)
                                putString("USER_IMAGE", user.profileImage)
                                putString("USER_PHONE", user.phone)
                                putString("USER_ADDRESS", user.address)
                                putString("USER_DOB", user.dob)
                                putString("LOGIN_ID", usernameInput) 
                                apply()
                            }

                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid Password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.tvWelcome.startAnimation(fadeIn)
        binding.card.startAnimation(slideUp)
    }
}
