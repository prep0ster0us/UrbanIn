package com.example.urbanin.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executor


class LoginFragment : Fragment() {

    private var existingUser: Boolean = false
    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isLoggedIn: Boolean = false

    // for biometrics
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo

    // save for future login
    private lateinit var prefManager: LoginPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentLoginBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        prefManager = LoginPreferenceManager(requireContext())
        
        isLoggedIn = (auth.currentUser != null)

        binding.loginBiometricButton.isVisible = prefManager.isBiometricEnabled()
        binding.loginBiometricButton.setOnClickListener {
            checkDeviceHasBiometrics()


            if (binding.loginViewUsnField.text.toString().isEmpty()) {
                binding.loginUsnLayout.error = "Please enter email address to continue!"
            } else {
                binding.loginUsnLayout.error = null
                checkDeviceHasBiometrics()
            }
        }

        binding.loginViewUsnField.doOnTextChanged { _, _, _, _ ->
            binding.loginUsnLayout.error = null
        }
    }

    private fun checkDeviceHasBiometrics() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "Biometric allowed")
                createAuthBiometricPrompt()
                createAuthPromptInfo()
                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e(TAG, "Device does not have biometric hardware!")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(TAG, "Biometric feature currently unavailable")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e(TAG, "Biometric not enabled yet on device")
                requestBiometricPrompt.launch(android.Manifest.permission.USE_BIOMETRIC)
            }

            else -> {
                Log.e(TAG, "BIOMETRIC UNKNOWN ERROR")
            }
        }
    }

    private val requestBiometricPrompt = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(TAG, "Biometric Permission: Granted")
        } else {
            Log.i(TAG, "Biometric Permission: Denied")
        }
    }

    private fun createAuthBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(
            requireActivity(),
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "Authentication: Success!")
                    // if successful,
                    // fetch login creds ( from saved shared preferences)
                    val (email, password) = prefManager.fetchLoginCreds()
                    binding.loginViewUsnField.setText(email)
                    binding.loginViewPwdField.setText(password)
                    // authenticate and login
                    matchCredentials()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication Failed!", Toast.LENGTH_SHORT)
                        .show()
                    Log.e(TAG, "Authentication: Fail!")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(TAG, errString.toString())
                }
            })
    }

    private fun createAuthPromptInfo() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Use Biometric")
            .setSubtitle("Login using biometric")
            .setNegativeButtonText("Cancel")
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginViewSubmitBtn.setOnClickListener {
            // check if credentials match
            checkIfExists()
            Log.d(TAG, "User status: $existingUser")
            if (existingUser) {
                matchCredentials()
            }
//                val isValid = checkCredentials();
//                if(isValid) {
//                    // navigate successfully to next page
//                    // TODO: decide next page based on context of fragment trigger
//                    findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginSuccessToSearch())
//                } else {
//                    // TODO: show dialog that user not registered yet, and ask if want to register
//                    // TODO: based on response, dismiss dialog (if no) or redirect to sign up (if yes)
//                    findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginToSignUp())
//                }
//
//            } else {
//                // navigate to sign up page
//                findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginToSignUp())
//            }
        }

        binding.loginViewSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginToSignUp())
        }
    }



    private fun checkIfExists() {
        db.collection("Users")
            .whereEqualTo("Email", binding.loginViewUsnField.text.toString())
            .get()
            .addOnSuccessListener { documents ->
                existingUser = true
                for (document in documents) {
                    Log.d(TAG, "User found! Document id: ${document.id}")
                    Toast.makeText(requireContext(), "User found!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                existingUser = false
                Log.w(TAG, "No User! Error: ${e.message}")
                Toast.makeText(requireContext(), "No user found", Toast.LENGTH_SHORT).show()
            }
    }

    private fun matchCredentials() {
        auth.signInWithEmailAndPassword(
            binding.loginViewUsnField.text.toString(),
            binding.loginViewPwdField.text.toString()
        ).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                Toast.makeText(
                    requireContext(),
                    "Welcome, ${auth.currentUser!!.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
                // check if any saved login credentials prior to login (based on which biometric prompt will be enabled)
                if(prefManager.isFirstLogin()) {
                    // save in shared preferences (for future login + biometric)
                    prefManager.writeLoginCreds(
                        binding.loginViewUsnField.text.toString(),
                        binding.loginViewPwdField.text.toString()
                    )
                    // TODO: show dialog to ask to enroll in biometric --> done in LandlordSearchFragment
                }
                // navigate successfully to next page
                // TODO: decide next page based on context of fragment trigger
                findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginSuccessToSearch())
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure ", task.exception)
                // clear password field
                binding.loginViewPwdField.setText("")
                Toast.makeText(
                    requireContext(),
                    "Wrong Credentials! Try again..",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: show dialog that user not registered yet, and ask if want to register

                // TODO: based on response, dismiss dialog (if no) or redirect to sign up (if yes)
//                    findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginToSignUp())
            }
        }
    }
}
