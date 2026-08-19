package com.example.smarthealthcare.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarthealthcare.Adapter.NearDoctorsAdapter
import com.example.smarthealthcare.Model.DoctorsModel
import com.example.smarthealthcare.R
import com.example.smarthealthcare.ViewModel.MainViewModel
import com.example.smarthealthcare.databinding.FragmentHomeBinding
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NearDoctorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        initNearDoctor()
        applyAnimations()
        
        val userName = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
            .getString("USER_NAME", "")
        if (!userName.isNullOrEmpty()) {
            binding.textView6.text = "Hello, $userName"
        }

        binding.exploreBtn.setOnClickListener {
            // Navigate to Explore Fragment via Bottom Navigation
            val bottomNav = activity?.findViewById<ChipNavigationBar>(R.id.bottomMenu)
            bottomNav?.setItemSelected(R.id.explorer)
        }
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        binding.headerLayout.startAnimation(fadeIn)
        binding.imageView3.startAnimation(slideUp)
        binding.exploreBtn.startAnimation(slideUp)
    }

    private fun initNearDoctor() {
        binding.progressBar4.visibility = View.VISIBLE
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NearDoctorsAdapter(mutableListOf())
        binding.recyclerView.adapter = adapter
        
        viewModel.loadDoctors().observe(viewLifecycleOwner) { doctors ->
            if (doctors != null) {
                // Show only top 4 doctors
                val limitedList = doctors.take(4)
                adapter.updateList(limitedList)
                binding.recyclerView.scheduleLayoutAnimation()
            }
            binding.progressBar4.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
