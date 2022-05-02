package com.danpike.socialapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.danpike.socialapp.databinding.FragmentSignUpBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstNameEditText = binding.fragmentSignupFirstNameInput
        val firstNameValidation = binding.fragmentSignupFirstNameValidation
        val lastNameEditText = binding.fragmentSignupLastNameInput
        val lastNameValidation = binding.fragmentSignupLastNameValidation
        val emailEditText = binding.fragmentSignupEmailInput
        val emailValidation = binding.fragmentEmailValidation
        val passwordEditText = binding.fragmentSignupPasswordInput
        val passwordValidation = binding.fragmentPasswordValidation
        val confirmPasswordEditText = binding.fragmentSignupConfirmPasswordInput
        val confirmPasswordValidation = binding.fragmentConfirmPasswordValidation
        val signUpButton = binding.fragmentSignupSignUpButton

        binding.fragmentSignupBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        fun checkIsBlankValidation(text: EditText, validationMessage: TextView) {
            if (text.text.isBlank()) {
                validationMessage.visibility = View.VISIBLE
            } else {
                validationMessage.visibility = View.INVISIBLE
            }
        }

        signUpButton.setOnClickListener {
            checkIsBlankValidation(firstNameEditText, firstNameValidation)
            checkIsBlankValidation(lastNameEditText, lastNameValidation)

            if (!emailEditText.text.contains("@")) {
                emailValidation.visibility = View.VISIBLE
            } else {
                emailValidation.visibility = View.INVISIBLE
            }

            if (passwordEditText.text.isBlank() || passwordEditText.text.length < 8) {
                passwordValidation.visibility = View.VISIBLE
            } else {
                passwordValidation.visibility = View.INVISIBLE
            }

            if (passwordEditText.text != confirmPasswordEditText.text) {
                confirmPasswordValidation.visibility = View.VISIBLE
            } else {
                confirmPasswordValidation.visibility = View.INVISIBLE
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}