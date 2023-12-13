package com.example.urbanin.roommates.account

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.databinding.FragmentAccountSettingsBinding
import com.example.urbanin.databinding.RoommateAccountSettingsBinding

class AccountSettings : Fragment() {

    private lateinit var binding: RoommateAccountSettingsBinding

    private lateinit var prefManager: LoginPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RoommateAccountSettingsBinding.inflate(layoutInflater)

        prefManager = LoginPreferenceManager(requireContext())

        // check if biometric enabled for user
        binding.setting1.isChecked = prefManager.isBiometricEnabled()
        // check current theme of app, and set setting toggle
        val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.setting2.isChecked = isDarkTheme

        binding.btnBackToAccount.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.setting1.setOnCheckedChangeListener { _, isChecked ->
            prefManager.setBiometricEnabled(isChecked)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_account_settings, container, false)
        binding.setting2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Enable dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.setting2.isChecked = true
            } else {
                // Enable light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.setting2.isChecked = false
            }
        }
        return binding.root
    }

}