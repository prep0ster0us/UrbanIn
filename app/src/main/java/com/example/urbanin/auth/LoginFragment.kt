package com.example.urbanin.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.LandlordActivity
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginViewSubmitBtn.setOnClickListener {
//            if (isAdded) {
            if (true) {
                // Add your login logic here

                // Navigate to LandlordFragment
                val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.navigate_login_to_landlord_search)
            }
        }

        binding.loginViewSignUp.setOnClickListener {
            if (isAdded) {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
