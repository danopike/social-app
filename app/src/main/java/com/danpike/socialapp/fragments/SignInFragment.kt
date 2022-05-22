package com.danpike.socialapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.danpike.socialapp.R
import com.danpike.socialapp.api.responses.UserResponse
import com.danpike.socialapp.databinding.FragmentSignInBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInFragment : BaseFragment() {

    private var _binding: FragmentSignInBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var _listener : ISignInFragmentListener

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _listener = context as ISignInFragmentListener

        val emailEt = binding.fragmentSigninEmailInput
        val passwordEt = binding.fragmentSigninPasswordInput
        val emailValidation = binding.fragmentSigninEmailValidation
        val passwordValidation = binding.fragmentSigninPasswordValidation
        val ipEditText = binding.fragmentSigninIpInput

        fun setErrorVisibility(text: TextView, error: Boolean) {
            if (error) {
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

            _listener.onSignIn(endPoint, emailEt.text.toString(), passwordEt.text.toString())
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

    fun handleUserResponse(response: Call<UserResponse>) {
        response.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
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

                    val action = SignInFragmentDirections.actionSignInFragmentToDashboardFragment(
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

    interface ISignInFragmentListener {
        fun onSignIn(endpoint: String, email: String, password: String)
    }
}