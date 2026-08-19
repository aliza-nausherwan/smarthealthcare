package com.example.smarthealthcare.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.smarthealthcare.databinding.FragmentExploreBinding
import java.util.*

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NearDoctorsAdapter
    private var allDoctors: List<DoctorsModel> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        loadAllDoctors()
        applyAnimations()
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        binding.textViewTitle.startAnimation(fadeIn)
        binding.searchCard.startAnimation(slideUp)
    }

    private fun setupRecyclerView() {
        binding.exploreRv.layoutManager = LinearLayoutManager(requireContext())
        adapter = NearDoctorsAdapter(mutableListOf())
        binding.exploreRv.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDoctors(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadAllDoctors() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.loadDoctors().observe(viewLifecycleOwner) { doctors ->
            if (doctors != null) {
                allDoctors = doctors
                adapter.updateList(doctors)
                binding.exploreRv.scheduleLayoutAnimation()
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun filterDoctors(query: String) {
        val filteredList = if (query.isEmpty()) {
            allDoctors
        } else {
            val lowercaseQuery = query.lowercase(Locale.getDefault())
            allDoctors.filter { doctor ->
                doctor.Name.lowercase(Locale.getDefault()).contains(lowercaseQuery) ||
                        doctor.Special.lowercase(Locale.getDefault()).contains(lowercaseQuery)
            }
        }
        adapter.updateList(filteredList)
        binding.exploreRv.scheduleLayoutAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
