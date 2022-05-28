package com.danpike.socialapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.danpike.socialapp.R
import com.danpike.socialapp.api.responses.UserResponse
import com.danpike.socialapp.databinding.FragmentSignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpFragment : BaseFragment() {

    private var _binding: FragmentSignUpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var _listener: ISignUpFragmentListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _listener = context as ISignUpFragmentListener

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
            findNavController().navigate(R.id.action_SignUpFragment_to_SignInFragment)
        }

        fun setErrorVisibility(text: TextView, error: Boolean) {
            if (error) {
                text.visibility = View.VISIBLE
            } else {
                text.visibility = View.GONE
            }
        }

        fun checkPassword() {
            setErrorVisibility(
                passwordValidation,
                passwordEditText.text.isBlank() || passwordEditText.text.length < 5
            )

            if (confirmPasswordEditText.text.isNotBlank() || confirmPasswordValidation.isVisible) {
                setErrorVisibility(
                    confirmPasswordValidation,
                    passwordEditText.text.toString() != confirmPasswordEditText.text.toString()
                )
            }
        }

        fun checkPasswordConfirm() {
            setErrorVisibility(
                confirmPasswordValidation,
                passwordEditText.text.toString() != confirmPasswordEditText.text.toString()
            )
        }

        firstNameEditText.addTextChangedListener {
            if (firstNameValidation.isVisible) {
                setErrorVisibility(firstNameValidation, firstNameEditText.text.isBlank())
            }
        }

        lastNameEditText.addTextChangedListener {
            if (lastNameValidation.isVisible) {
                setErrorVisibility(lastNameValidation, lastNameEditText.text.isBlank())
            }
        }

        emailEditText.addTextChangedListener {
            if (emailValidation.isVisible) {
                setErrorVisibility(emailValidation, !emailEditText.text.contains("@"))
            }
        }

        passwordEditText.addTextChangedListener {
            checkPassword()
        }

        confirmPasswordEditText.addTextChangedListener {
            checkPasswordConfirm()
        }

        signUpButton.setOnClickListener {
            setErrorVisibility(firstNameValidation, firstNameEditText.text.isBlank())
            setErrorVisibility(lastNameValidation, lastNameEditText.text.isBlank())
            setErrorVisibility(emailValidation, !emailEditText.text.contains("@"))
            checkPassword()

            if (passwordEditText.text.isNotBlank()) {
                checkPasswordConfirm()
            }

            if (firstNameValidation.isVisible || lastNameValidation.isVisible
                || emailValidation.isVisible || passwordValidation.isVisible
                || confirmPasswordValidation.isVisible
            ) {
                return@setOnClickListener
            }

            _listener.onSignUp(
                firstNameEditText.text.toString(),
                lastNameEditText.text.toString(),
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }

    fun handleSignUpResponse(response: Call<UserResponse>) {
        response.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.code() == 201) {
                    val user = response.body() ?: return

                    showAlertDialog(
                        getString(R.string.success_message_title),
                        getString(R.string.account_created)
                    )

                    val sharedPref =
                        activity?.getPreferences(Context.MODE_PRIVATE)

                    if (sharedPref != null) {
                        with(sharedPref.edit()) {
                            putString("token", user.token)
                            apply()
                        }
                    }

                    val action = SignUpFragmentDirections.actionSignUpFragmentToDashboardFragment(
                        user.firstName ?: "",
                        user.email ?: ""
                    )
                    findNavController().navigate(action)
                } else if (response.code() == 400) {
                    receivedErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface ISignUpFragmentListener {
        fun onSignUp(firstName: String, lastName: String, email: String, password: String)
    }
}