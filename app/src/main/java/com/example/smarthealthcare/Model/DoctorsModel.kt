package com.example.smarthealthcare.Model

import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class DoctorsModel(
    var Address: String = "",
    var Biography: String = "",
    var Id: String = "",
    var Name: String = "",
    var Image: String = "",
    var Special: String = "",
    var Experience: String = "",
    var Cost: String = "",
    var Date: String = "",
    var Time: String = "",
    var Location: String = "",
    var Phone: String = "",
    var patient: String = "",
    var Rating: Double = 0.0,
    var Site: String = ""
) : Parcelable {
    companion object {
        fun fromSnapshot(snapshot: DataSnapshot): DoctorsModel {
            val map = snapshot.value as? Map<String, Any> ?: return DoctorsModel()
            return DoctorsModel(
                Address = (map["Address"] ?: map["address"] ?: "").toString(),
                Biography = (map["Biography"] ?: map["biography"] ?: "").toString(),
                Id = (map["Id"] ?: map["id"] ?: "").toString(),
                Name = (map["Name"] ?: map["name"] ?: "").toString(),
                Image = (map["Image"] ?: map["image"] ?: "").toString(),
                Special = (map["Special"] ?: map["special"] ?: "").toString(),
                Experience = (map["Experience"] ?: map["experience"] ?: "").toString(),
                Cost = (map["Cost"] ?: map["cost"] ?: "").toString(),
                Date = (map["Date"] ?: map["date"] ?: "").toString(),
                Time = (map["Time"] ?: map["time"] ?: "").toString(),
                Location = (map["Location"] ?: map["location"] ?: "").toString(),
                Phone = (map["Phone"] ?: map["phone"] ?: "").toString(),
                patient = (map["patient"] ?: map["Patient"] ?: "").toString(),
                Rating = (map["Rating"] ?: map["rating"] ?: 0.0).toString().toDoubleOrNull() ?: 0.0,
                Site = (map["Site"] ?: map["site"] ?: "").toString()
            )
        }
    }
}
