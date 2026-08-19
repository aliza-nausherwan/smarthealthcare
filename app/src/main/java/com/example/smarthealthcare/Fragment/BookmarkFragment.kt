package com.example.smarthealthcare.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarthealthcare.Activity.DetailedActivity
import com.example.smarthealthcare.Adapter.AppointmentAdapter
import com.example.smarthealthcare.Model.AppointmentModel
import com.example.smarthealthcare.Model.DoctorsModel
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.FragmentBookmarkBinding
import com.google.firebase.database.*
import java.util.*

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AppointmentAdapter
    private var allAppointments = mutableListOf<AppointmentModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        applyAnimations()
        setupRecyclerView()
        setupSearch()
        loadAppointments()
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        binding.textViewTitle.startAnimation(fadeIn)
        binding.searchCard.startAnimation(slideUp)
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(
            appointments = mutableListOf(),
            onDetailsClick = { appointment ->
                showPatientDetails(appointment)
            },
            onCancelClick = { appointment ->
                showCancelConfirmation(appointment)
            },
            onDoctorClick = { doctorId ->
                fetchDoctorAndShowDetails(doctorId)
            }
        )
        binding.appointmentRv.layoutManager = LinearLayoutManager(requireContext())
        binding.appointmentRv.adapter = adapter
    }

    private fun fetchDoctorAndShowDetails(doctorId: String) {
        if (doctorId.isEmpty()) {
            Toast.makeText(context, "Doctor ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        val dbRef = FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com").getReference("Doctors").child(doctorId)
        
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar.visibility = View.GONE
                if (snapshot.exists()) {
                    val doctor = DoctorsModel.fromSnapshot(snapshot)
                    val intent = Intent(requireContext(), DetailedActivity::class.java)
                    intent.putExtra("object", doctor)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "Doctor information no longer available", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showPatientDetails(appointment: AppointmentModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_appointment, null)
        
        // Fixed IDs to match dialog_edit_appointment.xml
        val etName = dialogView.findViewById<EditText>(R.id.etPatientName)
        val etFatherName = dialogView.findViewById<EditText>(R.id.etFatherName)
        val etCnic = dialogView.findViewById<EditText>(R.id.etCnic)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        val etAge = dialogView.findViewById<EditText>(R.id.etAge)
        val spGender = dialogView.findViewById<Spinner>(R.id.spGender)

        // Pre-fill existing data
        etName.setText(appointment.patientName)
        etFatherName.setText(appointment.fatherName)
        etCnic.setText(appointment.cnic)
        etPhone.setText(appointment.phone)
        etEmail.setText(appointment.email)
        etAge.setText(appointment.age)
        
        val genderArray = resources.getStringArray(R.array.gender_array)
        val genderIndex = genderArray.indexOf(appointment.gender)
        if (genderIndex >= 0) {
            spGender.setSelection(genderIndex)
        }

        builder.setView(dialogView)
            .setPositiveButton("Update Data") { _, _ ->
                val updatedName = etName.text.toString().trim()
                val updatedFatherName = etFatherName.text.toString().trim()
                val updatedCnic = etCnic.text.toString().trim()
                val updatedPhone = etPhone.text.toString().trim()
                val updatedEmail = etEmail.text.toString().trim()
                val updatedAge = etAge.text.toString().trim()
                val updatedGender = spGender.selectedItem.toString()

                if (updatedName.isEmpty() || updatedCnic.isEmpty() || updatedPhone.isEmpty()) {
                    Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedAppointment = appointment.copy(
                    patientName = updatedName,
                    fatherName = updatedFatherName,
                    cnic = updatedCnic,
                    phone = updatedPhone,
                    email = updatedEmail,
                    age = updatedAge,
                    gender = updatedGender
                )
                
                updateAppointmentInFirebase(updatedAppointment)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateAppointmentInFirebase(appointment: AppointmentModel) {
        val dbRef = FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com").getReference("Appointments")
        dbRef.child(appointment.id).setValue(appointment).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Patient details updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearch() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAppointments(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterAppointments(query: String) {
        val filteredList = if (query.isEmpty()) {
            allAppointments
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            allAppointments.filter { 
                it.doctorName.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                it.patientName.lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }
        }
        adapter.updateList(filteredList)
        updateEmptyState(filteredList.isEmpty())
    }

    private fun loadAppointments() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loginId = sharedPreferences.getString("LOGIN_ID", "") ?: ""
        
        if (loginId.isEmpty()) {
            updateEmptyState(true)
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        val dbRef = FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com").getReference("Appointments")
        dbRef.orderByChild("userId").equalTo(loginId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allAppointments.clear()
                for (child in snapshot.children) {
                    val appointment = child.getValue(AppointmentModel::class.java)
                    if (appointment != null) allAppointments.add(appointment)
                }
                allAppointments.reverse()
                adapter.updateList(allAppointments)
                binding.progressBar.visibility = View.GONE
                updateEmptyState(allAppointments.isEmpty())
            }
            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun showCancelConfirmation(appointment: AppointmentModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Appointment")
            .setMessage("Cancel appointment with ${appointment.doctorName}?")
            .setPositiveButton("Yes") { _, _ ->
                FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com")
                    .getReference("Appointments").child(appointment.id).removeValue()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyView.visibility = View.VISIBLE
            binding.appointmentRv.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.appointmentRv.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
