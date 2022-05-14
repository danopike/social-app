package com.danpike.socialapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.danpike.socialapp.databinding.FragmentSignUpBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        var invalidField = false


        binding.fragmentSignupBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_SignUpFragment_to_SignInFragment)
        }

        fun setErrorVisibility(text: TextView, error: Boolean) {
            if (error) {
                invalidField = true
                text.visibility = View.VISIBLE
            } else {
                text.visibility = View.INVISIBLE
            }
        }

        passwordEditText.addTextChangedListener {
            setErrorVisibility(
                passwordValidation,
                passwordEditText.text.isBlank() || passwordEditText.text.length < 8
            )

            if (confirmPasswordEditText.text.isNotEmpty() || confirmPasswordValidation.isVisible) {
                setErrorVisibility(
                    confirmPasswordValidation,
                    passwordEditText.text != confirmPasswordEditText.text
                )
            }
        }

        confirmPasswordEditText.addTextChangedListener {
            setErrorVisibility(
                confirmPasswordValidation,
                passwordEditText.text != confirmPasswordEditText.text
            )
        }

        signUpButton.setOnClickListener {
            setErrorVisibility(firstNameValidation, firstNameEditText.text.isBlank())
            setErrorVisibility(lastNameValidation, lastNameEditText.text.isBlank())
            setErrorVisibility(emailValidation, !emailEditText.text.contains("@"))

            if (invalidField) {
                return@setOnClickListener
            }

            val sharedPref =
                activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            val ip = sharedPref.getString("ip_address", "")
            val endPoint = "http://$ip:5000/api/"

            val client = OkHttpClient.Builder().build()

            val retrofit = Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val service = retrofit.create(ApiInterface::class.java)

            val response = service.signUp(
                firstNameEditText.text.toString(),
                lastNameEditText.text.toString(),
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )

            response.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 201) {
                        val user = response.body() ?: return

                        showAlertDialog(getString(R.string.success_message_title), getString(R.string.account_created))

                        with(sharedPref.edit()) {
                            putString("token", user.token)
                            apply()
                        }

                        val action = SignUpFragmentDirections.actionSignUpFragmentToChatFragment(
                            user.firstName ?: "",
                            user.email ?: ""
                        )
                        findNavController().navigate(action)
                    } else if (response.code() == 400) {
                        val type = object : TypeToken<SignUpErrorResponse>() {}.type
                        val signUpResponse: SignUpErrorResponse? =
                            Gson().fromJson(response.errorBody()?.charStream(), type)
                        val message = signUpResponse?.message

                        if (message != null) {
                            showAlertDialog(
                                getString(R.string.error_message_title),
                                message
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.error_message_negative), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}