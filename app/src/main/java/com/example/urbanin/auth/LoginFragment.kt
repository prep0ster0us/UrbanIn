package com.example.urbanin.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.urbanin.BuildConfig
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executor


class LoginFragment : Fragment() {
    // TODO: <known flaw> the Google sign in doesn't work, we need to initialize with O2Auth API key to get response, but didn't work on this personally
    //  so gotten around to fix it

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // for biometrics
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo

    // save for future login
    private lateinit var prefManager: LoginPreferenceManager

    // for google sign-in
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        configureGoogleSignIn()

        prefManager = LoginPreferenceManager(requireContext())
        // hide nav bar, based on user mode
        when(prefManager.getUserMode()) {
            "tenant" -> SearchListingUtil.setTenantNavBarVisibility(requireActivity(), false)
            "landlord" -> SearchListingUtil.setLandlordNavBarVisibility(requireActivity(), false)
        }

        binding.loginBiometricButton.isVisible = prefManager.isBiometricEnabled()

        binding.loginBiometricButton.setOnClickListener {
            checkDeviceHasBiometrics()
        }

        binding.loginViewUsnField.doOnTextChanged { _, _, _, _ ->
            binding.loginUsnLayout.error = null
        }
    }

    private fun configureGoogleSignIn() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.MAPS_API_KEY)
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun navigateToNext() {
        when (prefManager.getRedirectContext()) {
            "Mode selection" -> findNavController().navigate(LoginFragmentDirections.navigateLoginSuccessToSearch())
            "Saved" -> {
                SearchListingUtil.setTenantNavBarVisibility(requireActivity(), true)
                findNavController().navigate(LoginFragmentDirections.navigateLoginToSaved())
            }
            "Message" -> {
                SearchListingUtil.setTenantNavBarVisibility(requireActivity(), true)
                findNavController().navigate(LoginFragmentDirections.navigateLoginToMessage())
            }
            "tenant_account" -> {
                SearchListingUtil.setTenantNavBarVisibility(requireActivity(), true)
                findNavController().navigate(LoginFragmentDirections.navigateLoginSuccessToSearch())
            }
            "landlord_account" -> {
                SearchListingUtil.setLandlordNavBarVisibility(requireActivity(), true)
                findNavController().navigate(LoginFragmentDirections.navigateLoginSuccessToSearch())
            }
            "tenant_detailed_listing" -> {
                findNavController().popBackStack()
            }
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
        biometricPrompt = BiometricPrompt(requireActivity(),
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
        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Use Biometric")
            .setSubtitle("Login using biometric").setNegativeButtonText("Cancel").build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginViewSubmitBtn.setOnClickListener {
            if(binding.loginRememberLoginCheck.isChecked) {
                prefManager.writeLoginCreds(
                    binding.loginViewUsnField.text.toString(),
                    binding.loginViewPwdField.text.toString()
                )
            }
            // check if credentials match
            binding.loginUsnLayout.error = if(binding.loginViewUsnField.text.toString().isEmpty()) "Please enter your email address" else null
            binding.loginPwdLayout.error = if(binding.loginViewPwdField.text.toString().isEmpty()) "Please enter your password" else null
            if(binding.loginUsnLayout.error == null) {
                if(binding.loginPwdLayout.error == null) {
                    hideKeyboard()
                    matchCredentials()
                }
            }
        }

        binding.signUpViewGoogle.setOnClickListener {
            googleSignIn()
        }

        binding.loginViewForgotPwd.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.navigateLoginToForgotPwd())
        }

        binding.loginViewSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.navigateLoginToSignUp())
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun matchCredentials() {
        auth.signInWithEmailAndPassword(
            binding.loginViewUsnField.text.toString(), binding.loginViewPwdField.text.toString()
        ).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                Toast.makeText(
                    requireContext(),
                    "Welcome, ${auth.currentUser!!.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
                prefManager.setLoggedIn(true)
                prefManager.setRememberLogin(binding.loginRememberLoginCheck.isChecked)
                if(prefManager.isBiometricEnabled()) {
                    // save in shared preferences (for future login + biometric)
                    prefManager.writeLoginCreds(
                        binding.loginViewUsnField.text.toString(),
                        binding.loginViewPwdField.text.toString()
                    )
                }
                // navigate successfully to next page
                navigateToNext()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure ", task.exception)
                // clear password field
                binding.loginViewPwdField.setText("")
                Toast.makeText(
                    requireContext(), "Wrong Credentials! Try again..", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                navigateToNext()
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1001
    }
}
