package com.example.smarthealthcare.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppointmentModel(
    val id: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val patientName: String = "",
    val fatherName: String = "",
    val phone: String = "",
    val email: String = "",
    val cnic: String = "",
    val gender: String = "",
    val age: String = "",
    val date: String = "",
    val day: String = "",
    val time: String = "",
    val status: String = "Pending",
    val userId: String = ""
) : Parcelable
