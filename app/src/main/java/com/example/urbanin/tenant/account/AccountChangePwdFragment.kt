package com.example.urbanin.tenant.account

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
                findNavController().navigate(AccountChangePwdFragmentDirections.navigateChangePwdBackToAccountTenant())
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
                    val successDialog = MaterialAlertDialogBuilder(requireContext())
                        .setIcon(com.google.android.material.R.drawable.mtrl_ic_check_mark)
                        .setTitle("Success!")
                        .setMessage("Password updated successfully")
                        .show()
                    // dismiss after 2s
                    val timer = object: CountDownTimer(2000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            successDialog.dismiss()
                            findNavController().navigate(AccountChangePwdFragmentDirections.navigateChangePwdBackToAccountTenant())
                        }
                    }
                    timer.start()
                } else {
                    confirmNewPwdLayout.error = "Passwords do not match"
                }
            }
        }
        return binding.root
    }

}