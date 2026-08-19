package com.example.smarthealthcare.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.smarthealthcare.Model.DoctorsModel
import com.google.firebase.database.*

class MainRepository {

    private val firebaseDatabase =
        FirebaseDatabase.getInstance("https://aliza12345678-default-rtdb.firebaseio.com")

    fun load(): LiveData<MutableList<DoctorsModel>> {
        val liveData = MutableLiveData<MutableList<DoctorsModel>>()

        val ref = firebaseDatabase.getReference("Doctors")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<DoctorsModel>()
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        Log.d("MainRepository", "Child found: ${child.key} -> ${child.value}")
                        // Using the manual mapping fromSnapshot to handle case-sensitivity (Image vs image, etc.)
                        val item = DoctorsModel.fromSnapshot(child)
                        list.add(item)
                    }
                    Log.d("MainRepository", "Total doctors loaded: ${list.size}")
                } else {
                    Log.w("MainRepository", "No data found at 'Doctors' node")
                }
                liveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainRepository", "Database error: ${error.message}")
            }
        })

        return liveData
    }
}
