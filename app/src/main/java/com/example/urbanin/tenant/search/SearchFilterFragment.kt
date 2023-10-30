package com.example.urbanin.tenant.search

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentSearchFilterBinding

// as independent fragment

class SearchFilterFragment: Fragment() {
//    private var _binding: FragmentSearchFilterBinding? = null
//    private val binding get() = _binding!!

    private lateinit var binding: FragmentSearchFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchFilterBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFilterBack.setOnClickListener {
            findNavController().navigate(SearchFilterFragmentDirections.actionSearchFilterFragmentToSearchFragment())
        }
    }
}
