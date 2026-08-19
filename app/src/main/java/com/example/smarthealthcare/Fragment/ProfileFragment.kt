package com.example.smarthealthcare.Fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.smarthealthcare.Activity.LoginActivity
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import java.util.Calendar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            binding.profileImg.setImageURI(selectedUri)
            saveProfileImageToDatabase(selectedUri.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyAnimations()
        loadUserInfo()

        binding.profileImg.setOnClickListener { pickImage.launch("image/*") }
        binding.btnEditPhoto.setOnClickListener { pickImage.launch("image/*") }
        binding.btnDeletePhoto.setOnClickListener { showDeletePhotoConfirmation() }

        binding.menuEditProfile.setOnClickListener { showEditProfileDialog() }

        binding.menuAppointments.setOnClickListener {
            activity?.findViewById<ChipNavigationBar>(R.id.bottomMenu)?.setItemSelected(R.id.bookmark)
        }

        binding.menuPrivacy.setOnClickListener { showInfoSheet("Privacy Policy") }
        binding.menuHelp.setOnClickListener { showInfoSheet("Help & Support") }

        binding.logoutBtn.setOnClickListener { logoutUser() }
        binding.btnDeleteAccount.setOnClickListener { showDeleteAccountConfirmation() }
    }

    private fun loadUserInfo() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loginId = sharedPreferences.getString("LOGIN_ID", "Username")
        val userName = sharedPreferences.getString("USER_NAME", "User Name")
        val userImage = sharedPreferences.getString("USER_IMAGE", "")
        val userPhone = sharedPreferences.getString("USER_PHONE", "Not Set")
        val userAddress = sharedPreferences.getString("USER_ADDRESS", "Not Set")
        val userDob = sharedPreferences.getString("USER_DOB", "Not Set")

        binding.textViewTitle.text = loginId
        binding.profileName.text = userName
        binding.profilePhone.text = userPhone
        binding.profileDob.text = userDob
        binding.profileAddress.text = userAddress

        if (!userImage.isNullOrEmpty()) {
            Glide.with(this).load(userImage).placeholder(R.drawable.white_circle_bg).into(binding.profileImg)
            binding.btnDeletePhoto.visibility = View.VISIBLE
        } else {
            binding.profileImg.setImageResource(R.drawable.white_circle_bg)
            binding.btnDeletePhoto.visibility = View.GONE
        }
    }

    private fun showInfoSheet(type: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_info_sheet, null)
        
        val title = view.findViewById<TextView>(R.id.sheetTitle)
        val content = view.findViewById<TextView>(R.id.sheetContent)
        val icon = view.findViewById<ImageView>(R.id.sheetIcon)
        val btnAction = view.findViewById<Button>(R.id.btnAction)
        val btnClose = view.findViewById<Button>(R.id.btnSheetClose)

        if (type == "Privacy Policy") {
            title.text = "Privacy Policy"
            icon.setImageResource(android.R.drawable.ic_lock_idle_lock)
            btnAction.visibility = View.GONE
            content.text = """
                1. Data Protection: All medical data is encrypted.
                2. Data Sharing: Your records are only shared with doctors you book.
                3. Camera Access: Only used for profile photos.
                4. Location: Used to show nearby medical centers.
                
                For full legal details, visit our website.
            """.trimIndent()
        } else {
            title.text = "Help & Support"
            icon.setImageResource(android.R.drawable.ic_menu_help)
            btnAction.visibility = View.VISIBLE
            btnAction.text = "Email Support"
            btnAction.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:support@smarthealthcare.com")
                    putExtra(Intent.EXTRA_SUBJECT, "App Support Request")
                }
                startActivity(Intent.createChooser(emailIntent, "Send Email"))
            }
            content.text = """
                - Fast Booking: Tap 'Home' and pick a top doctor.
                - Updates: Change your details in 'Edit Profile'.
                - Connectivity: Ensure you have active internet for booking.
                
                Need more help? Tap the button below to email us.
            """.trimIndent()
        }

        btnClose.setOnClickListener { bottomSheetDialog.dismiss() }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun showEditProfileDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<EditText>(R.id.etEditName)
        val etDob = dialogView.findViewById<EditText>(R.id.etEditDob)
        val etPhone = dialogView.findViewById<EditText>(R.id.etEditPhone)
        val etAddress = dialogView.findViewById<EditText>(R.id.etEditAddress)
        
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        etName.setText(sharedPreferences.getString("USER_NAME", ""))
        etDob.setText(sharedPreferences.getString("USER_DOB", ""))
        etPhone.setText(sharedPreferences.getString("USER_PHONE", ""))
        etAddress.setText(sharedPreferences.getString("USER_ADDRESS", ""))

        etDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                etDob.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        builder.setView(dialogView).setTitle("Update Profile")
            .setPositiveButton("Save") { _, _ ->
                val newName = etName.text.toString().trim()
                if (newName.isNotEmpty()) updateProfileData(newName, etDob.text.toString(), etPhone.text.toString(), etAddress.text.toString())
            }.setNegativeButton("Cancel", null).show()
    }

    private fun updateProfileData(name: String, dob: String, phone: String, address: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loginId = sharedPreferences.getString("LOGIN_ID", "") ?: ""
        if (loginId.isEmpty()) return

        with(sharedPreferences.edit()) {
            putString("USER_NAME", name)
            putString("USER_DOB", dob)
            putString("USER_PHONE", phone)
            putString("USER_ADDRESS", address)
            apply()
        }
        
        FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com")
            .getReference("Users").child(loginId).updateChildren(mapOf(
                "username" to name, "dob" to dob, "phone" to phone, "address" to address
            )).addOnSuccessListener { loadUserInfo() }
    }

    private fun saveProfileImageToDatabase(uri: String) {
        val loginId = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("LOGIN_ID", "") ?: ""
        requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().putString("USER_IMAGE", uri).apply()
        if (loginId.isNotEmpty()) {
             FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com")
                .getReference("Users").child(loginId).child("profileImage").setValue(uri)
        }
        binding.btnDeletePhoto.visibility = View.VISIBLE
    }

    private fun showDeletePhotoConfirmation() {
        AlertDialog.Builder(requireContext()).setTitle("Remove Photo").setPositiveButton("Remove") { _, _ -> 
            val loginId = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("LOGIN_ID", "") ?: ""
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().putString("USER_IMAGE", "").apply()
            if (loginId.isNotEmpty()) FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com").getReference("Users").child(loginId).child("profileImage").setValue("")
            binding.profileImg.setImageResource(R.drawable.white_circle_bg)
            binding.btnDeletePhoto.visibility = View.GONE
        }.setNegativeButton("Cancel", null).show()
    }

    private fun showDeleteAccountConfirmation() {
        AlertDialog.Builder(requireContext()).setTitle("Delete Account").setMessage("Permanent action. Continue?").setPositiveButton("Delete") { _, _ -> 
            val loginId = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("LOGIN_ID", "") ?: ""
            if (loginId.isNotEmpty()) FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com").getReference("Users").child(loginId).removeValue().addOnSuccessListener { logoutUser() }
        }.setNegativeButton("Cancel", null).show()
    }

    private fun logoutUser() {
        requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()
        startActivity(Intent(requireContext(), LoginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        binding.profileCard.startAnimation(slideUp)
        binding.textViewTitle.startAnimation(fadeIn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
