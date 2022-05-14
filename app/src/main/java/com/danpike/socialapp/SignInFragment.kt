package com.danpike.socialapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.danpike.socialapp.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

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

        val ipEditText = binding.fragmentSigninIpInput

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