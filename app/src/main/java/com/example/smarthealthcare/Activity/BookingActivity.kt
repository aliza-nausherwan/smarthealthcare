package com.example.smarthealthcare.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthealthcare.Model.AppointmentModel
import com.example.smarthealthcare.Model.DoctorsModel
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.ActivityBookingBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding
    private lateinit var doctor: DoctorsModel
    private var selectedDate = ""
    private var selectedDay = ""
    private var selectedTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctor = intent.getParcelableExtra("doctor")!!

        applyAnimations()
        setupPickers()
        setupBooking()

        binding.backBtn.setOnClickListener { finish() }
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        
        binding.constraintLayout.startAnimation(fadeIn)
        binding.confirmBtn.startAnimation(slideUp)
    }

    private fun setupPickers() {
        val calendar = Calendar.getInstance()

        binding.dateTxt.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(year, month, day)
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                
                selectedDate = dateFormat.format(selectedCal.time)
                selectedDay = dayFormat.format(selectedCal.time)
                
                binding.dateTxt.text = "$selectedDate ($selectedDay)"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.timeTxt.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                val timeCal = Calendar.getInstance()
                timeCal.set(Calendar.HOUR_OF_DAY, hour)
                timeCal.set(Calendar.MINUTE, minute)
                
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                selectedTime = timeFormat.format(timeCal.time)
                binding.timeTxt.text = selectedTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
    }

    private fun setupBooking() {
        binding.confirmBtn.setOnClickListener {
            val name = binding.patientNameEt.text.toString().trim()
            val fatherName = binding.fatherNameEt.text.toString().trim()
            val cnic = binding.cnicEt.text.toString().trim()
            val phone = binding.phoneEt.text.toString().trim()
            val email = binding.emailEt.text.toString().trim()
            val age = binding.ageEt.text.toString().trim()
            val gender = binding.genderSpinner.selectedItem.toString()

            if (name.isEmpty() || fatherName.isEmpty() || cnic.isEmpty() || phone.isEmpty() || 
                email.isEmpty() || age.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cnic.length != 13) {
                binding.cnicEt.error = "CNIC must be 13 digits"
                return@setOnClickListener
            }

            if (phone.length != 11) {
                binding.phoneEt.error = "Phone number must be 11 digits"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEt.error = "Invalid Email Address"
                return@setOnClickListener
            }

            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            // Use LOGIN_ID for permanent tracking
            val userId = sharedPreferences.getString("LOGIN_ID", "") ?: ""

            val dbRef = FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com").getReference("Appointments")
            val id = dbRef.push().key ?: ""

            val appointment = AppointmentModel(
                id = id,
                doctorId = doctor.Id,
                doctorName = doctor.Name,
                patientName = name,
                fatherName = fatherName,
                phone = phone,
                email = email,
                cnic = cnic,
                gender = gender,
                age = age,
                date = selectedDate,
                day = selectedDay,
                time = selectedTime,
                userId = userId
            )

            dbRef.child(id).setValue(appointment).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Appointment Booked Successfully", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Booking Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
