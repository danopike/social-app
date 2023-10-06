package com.danpike.socialapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.danpike.socialapp.R
import com.danpike.socialapp.api.responses.UserResponse
import com.danpike.socialapp.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : BaseFragment() {
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var _listener: IDashboardFragmentListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DashboardFragmentArgs by navArgs()

        _listener = context as IDashboardFragmentListener

        binding.fragmentDashboardWelcomeText.text = getString(
            R.string.chat_welcome_message,
            args.firstName
        )

        val addFriendInput = binding.fragmentDashboardFriendInput
        val addFriendButton = binding.fragmentDashboardAddFriendButton
        val addFriendValidation = binding.fragmentDashboardFriendValidation

        addFriendButton.setOnClickListener {
            if (addFriendInput.text.isNullOrBlank()) {
                addFriendValidation.visibility = View.VISIBLE
            } else {
                _listener.onAddFriend(addFriendInput.text.toString())
            }
        }
    }

    fun handleAddFriendResponse(response: Call<UserResponse>) {
        response.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.code() == 200) {
                    val user = response.body() ?: return

                    showAlertDialog(
                        getString(R.string.success_message_title),
                        getString(R.string.dashboard_friend_added)
                    )
                } else if (response.code() == 400 || response.code() == 404) {
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

    interface IDashboardFragmentListener {
        fun onAddFriend(email: String)
    }
}