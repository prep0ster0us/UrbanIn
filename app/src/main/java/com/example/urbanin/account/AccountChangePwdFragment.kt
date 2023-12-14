package com.example.urbanin.account

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.urbanin.R
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.databinding.FragmentAccountChangePwdBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AccountChangePwdFragment : Fragment() {

    private lateinit var binding: FragmentAccountChangePwdBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var prefManager: LoginPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentAccountChangePwdBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        prefManager = LoginPreferenceManager(requireContext())

        with(binding) {

            btnBackToAccount.setOnClickListener {
                findNavController().navigate(AccountChangePwdFragmentDirections.navigateChangePwdBackToAccount())
            }

            confirmNewPwdInput.doOnTextChanged { _, _, _, _ ->
                if(newPwdInput.text.toString().isNotEmpty()) {
                    if(confirmNewPwdInput.text.toString() != newPwdInput.text.toString()) {
                        confirmNewPwdLayout.error = "Passwords do not match"
                    } else {
                        confirmNewPwdLayout.error = null
                    }
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        with(binding) {
            btnUpdatePwd.setOnClickListener {
                if(newPwdInput.text.toString() == confirmNewPwdInput.text.toString()) {
                    auth.currentUser!!.updatePassword(newPwdInput.text.toString())
                    // check if credentials stored in Shared Preferences, and update
                    if(auth.currentUser!!.email == prefManager.fetchLoginCreds().first) {
                        prefManager.writeLoginCreds(
                            auth.currentUser!!.email.toString(),
                            newPwdInput.text.toString()
                        )
                    }
                    showSuccessDialog()
                } else {
                    confirmNewPwdLayout.error = "Passwords do not match"
                }
            }
        }
        return binding.root
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.success_dialog_layout,null)
        val dialogIcon = view.findViewById<ImageView>(R.id.successIcon)
        Glide.with(requireContext()).load(R.drawable.success_dialog_icon).into(dialogIcon)
        val dialogMsg = view.findViewById<TextView>(R.id.successMessage)
        dialogMsg.text = "Password updated"
        builder.setView(view)
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        builder.show()
        // show dialog for 3s
        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisSeconds: Long) {}
            override fun onFinish() {
                builder.dismiss()
                findNavController().navigate(AccountChangePwdFragmentDirections.navigateChangePwdBackToAccount())
            }
        }
        timer.start()
    }

}