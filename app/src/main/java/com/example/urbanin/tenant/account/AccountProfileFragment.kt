package com.example.urbanin.tenant.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentAccountProfileBinding

class AccountProfileFragment : Fragment() {
    private lateinit var binding: FragmentAccountProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAccountProfileBinding.inflate(layoutInflater)

        with(binding) {
            btnBackToAccount.setOnClickListener {
                val action = AccountProfileFragmentDirections.navigateProfileBackToAccountTenant()
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

}