package com.example.smarthealthcare.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthealthcare.Model.AppointmentModel
import com.example.smarthealthcare.databinding.ViewholderAppointmentBinding

class AppointmentAdapter(
    private var appointments: List<AppointmentModel>,
    private val onDetailsClick: (AppointmentModel) -> Unit,
    private val onCancelClick: (AppointmentModel) -> Unit,
    private val onDoctorClick: (String) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderAppointmentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appointments[position]
        holder.binding.doctorNameTxt.text = item.doctorName
        holder.binding.patientNameTxt.text = "Patient: ${item.patientName}"
        holder.binding.dateTimeTxt.text = "${item.date} (${item.day}) | ${item.time}"
        holder.binding.statusTxt.text = item.status

        holder.binding.doctorNameTxt.setOnClickListener {
            onDoctorClick(item.doctorId)
        }

        holder.binding.detailsBtn.setOnClickListener {
            onDetailsClick(item)
        }

        holder.binding.cancelBtn.setOnClickListener {
            onCancelClick(item)
        }
    }

    override fun getItemCount(): Int = appointments.size

    fun updateList(newList: List<AppointmentModel>) {
        appointments = newList
        notifyDataSetChanged()
    }
}
