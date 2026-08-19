package com.example.smarthealthcare.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.smarthealthcare.Model.DoctorsModel
import com.example.smarthealthcare.Repository.MainRepository

class MainViewModel : ViewModel() {

    private val repository = MainRepository()

    fun loadDoctors(): LiveData<MutableList<DoctorsModel>> {

        return repository.load()
    }
}