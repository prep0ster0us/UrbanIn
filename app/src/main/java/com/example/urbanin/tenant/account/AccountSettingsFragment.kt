package com.example.urbanin.tenant.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentAccountSettingsBinding

class AccountSettingsFragment : Fragment() {

    private lateinit var binding: FragmentAccountSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAccountSettingsBinding.inflate(layoutInflater)

        binding.btnBackToAccount.setOnClickListener {
            val action = AccountSettingsFragmentDirections.navigateSettingsBackToAccountTenant()
            findNavController().navigate(action)
        }

//        binding.setting1Btn.setOnClickListener{
//            // handle setting toggle
//        }
//
//        binding.setting2Btn.setOnClickListener {
//            // handle setting toggle
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_account_settings, container, false)
        return binding.root
    }

}