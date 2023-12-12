package com.example.urbanin.account

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.NotificationPreferenceManager
import com.example.urbanin.databinding.FragmentAccountNotificationsBinding
import com.example.urbanin.databinding.FragmentAccountSettingsBinding

class AccountNotificationFragment : Fragment() {

    private lateinit var binding: FragmentAccountNotificationsBinding

    private lateinit var prefManager: NotificationPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentAccountNotificationsBinding.inflate(layoutInflater)
        prefManager = NotificationPreferenceManager(requireContext())

        binding.notif1.isChecked = prefManager.msgAlertEnabled()
        binding.notif2.isChecked = prefManager.listingAlertEnabled()

        binding.btnBackToAccount.setOnClickListener {
            val action = AccountSettingsFragmentDirections.navigateSettingsBackToAccount()
            findNavController().navigate(action)
        }

        binding.notif1.setOnCheckedChangeListener { _, isChecked ->
            prefManager.setMsgAlert(isChecked)
        }
        binding.notif2.setOnCheckedChangeListener { _, isChecked ->
            prefManager.setListingAlert(isChecked)
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