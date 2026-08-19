package com.example.smarthealthcare.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.smarthealthcare.Activity.DetailedActivity
import com.example.smarthealthcare.Model.DoctorsModel
import com.example.smarthealthcare.R
import com.example.smarthealthcare.databinding.ViewholderNearbyDoctorBinding

class NearDoctorsAdapter(
    private var items: List<DoctorsModel>
) : RecyclerView.Adapter<NearDoctorsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderNearbyDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderNearbyDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.nameTxt.text = item.Name
        holder.binding.specialTxt.text = item.Special
        holder.binding.costTxt.text = "Rs. ${item.Cost}"

        var imageUrl = item.Image
        // Fix for ImgBB viewer links: https://ibb.co/Vp0bxtrH -> https://i.ibb.co/Vp0bxtrH/image.png
        if (imageUrl.contains("ibb.co") && !imageUrl.contains("i.ibb.co")) {
            val code = imageUrl.substringAfter("ibb.co/")
            imageUrl = "https://i.ibb.co/$code/image.png"
        }

        Glide.with(context)
            .load(imageUrl)
            .apply(RequestOptions().transform(CenterCrop()))
            .placeholder(R.drawable.ic_launcher_background)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.binding.img)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("object", item)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<DoctorsModel>) {
        items = newList
        notifyDataSetChanged()
    }
}
