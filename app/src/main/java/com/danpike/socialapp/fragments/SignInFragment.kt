package com.danpike.socialapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.danpike.socialapp.R
import com.danpike.socialapp.api.ApiInterface
import com.danpike.socialapp.api.responses.SignUpErrorResponse
import com.danpike.socialapp.api.responses.User
import com.danpike.socialapp.databinding.FragmentSignInBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInFragment : BaseFragment() {

    private var _binding: FragmentSignInBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEt = binding.fragmentSigninEmailInput
        val passwordEt = binding.fragmentSigninPasswordInput
        val emailValidation = binding.fragmentSigninEmailValidation
        val passwordValidation = binding.fragmentSigninPasswordValidation
        val ipEditText = binding.fragmentSigninIpInput
        var invalidField = false

        fun setErrorVisibility(text: TextView, error: Boolean) {
            if (error) {
                invalidField = true
                text.visibility = View.VISIBLE
            } else {
                text.visibility = View.GONE
            }
        }

        binding.fragmentSignInLogInButton.setOnClickListener {
            setErrorVisibility(emailValidation, emailEt.text.isNullOrBlank())
            setErrorVisibility(passwordValidation, passwordEt.text.isNullOrBlank())

            if (emailEt.text.isNullOrBlank() || passwordEt.text.isNullOrBlank()) {
                return@setOnClickListener
            }

            val endPoint = "http://${ipEditText.text}:5000/api/"

            val client = OkHttpClient.Builder().build()

            val retrofit = Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val service = retrofit.create(ApiInterface::class.java)

            val response = service.signIn(
                emailEt.text.toString(),
                passwordEt.text.toString()
            )

            response.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200) {
                        val user = response.body() ?: return

                        val sharedPref =
                            activity?.getPreferences(Context.MODE_PRIVATE)

                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                putString("token", user.token)
                                apply()
                            }
                        }

                        val action = SignInFragmentDirections.actionSignInFragmentToChatFragment(
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

        binding.fragmentSignInSignUpButton.setOnClickListener {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            with(sharedPref.edit()) {
                putString("ip_address", ipEditText.text.toString())
                apply()
            }
            findNavController().navigate(R.id.action_SignInFragment_to_SignUpFragment)
        }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        ipEditText.setText(sharedPref.getString("ip_address", ""))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}