package com.example.urbanin.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentForgotPasswordBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPwdFragment : Fragment() {
    // TODO: <NOTE TO REVIEWER> never worked on this, so didn't get around to fix

    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)

        mAuth = FirebaseAuth.getInstance()

        // Reset Button Listener
        binding.btnReset.setOnClickListener {
            val strEmail = binding.edtForgotPasswordEmail.text.toString().trim()
            if (!TextUtils.isEmpty(strEmail)) {
                resetPassword(strEmail)
            } else {
                binding.txtLayoutEmail.error = "Email address can't be empty"
            }
        }

        binding.edtForgotPasswordEmail.doOnTextChanged { _, _, _, _ ->
            if (binding.edtForgotPasswordEmail.text.toString().isEmpty()) {
                binding.txtLayoutEmail.error = null
            }
        }

        // Back Button Listener
        binding.backToLoginButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    private fun resetPassword(email: String) {
        binding.forgetPasswordProgressbar.visibility = View.VISIBLE
        binding.btnReset.visibility = View.GONE

        mAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Snackbar.make(
                    binding.root,
                    "Reset Password link has been sent to your registered Email",
                    Snackbar.LENGTH_SHORT
                ).show()
                // Navigate to LoginActivity or other relevant actions
                // Example: findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Error :- ${e.message}", Toast.LENGTH_SHORT).show()
                binding.forgetPasswordProgressbar.visibility = View.GONE
                binding.btnReset.visibility = View.VISIBLE
            }
    }
}
