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
import androidx.fragment.app.Fragment
import com.example.urbanin.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth

class ForgotPwdFragment : Fragment() {

    private lateinit var btnReset: Button
    private lateinit var btnBack: Button
    private lateinit var edtEmail: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        // Initialization
        btnBack = view.findViewById(R.id.btnForgotPasswordBack)
        btnReset = view.findViewById(R.id.btnReset)
        edtEmail = view.findViewById(R.id.edtForgotPasswordEmail)
        progressBar = view.findViewById(R.id.forgetPasswordProgressbar)

        mAuth = FirebaseAuth.getInstance()

        // Reset Button Listener
        btnReset.setOnClickListener {
            val strEmail = edtEmail.text.toString().trim()
            if (!TextUtils.isEmpty(strEmail)) {
                resetPassword(strEmail)
            } else {
                edtEmail.error = "Email field can't be empty"
            }
        }

        // Back Button Listener
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun resetPassword(email: String) {
        progressBar.visibility = View.VISIBLE
        btnReset.visibility = View.INVISIBLE

        mAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(
                    activity,
                    "Reset Password link has been sent to your registered Email",
                    Toast.LENGTH_SHORT
                ).show()
                // Navigate to LoginActivity or other relevant actions
                // Example: findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Error :- ${e.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
                btnReset.visibility = View.VISIBLE
            }
    }
}
