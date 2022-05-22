package com.danpike.socialapp.fragments

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.danpike.socialapp.R
import com.danpike.socialapp.api.responses.ErrorResponse
import com.danpike.socialapp.api.responses.UserResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response

abstract class BaseFragment : Fragment() {
    fun receivedErrorResponse(response: Response<UserResponse>) {
        val type = object : TypeToken<ErrorResponse>() {}.type
        val signUpResponse: ErrorResponse? =
            Gson().fromJson(response.errorBody()?.charStream(), type)
        val message = signUpResponse?.message

        if (message != null) {
            showAlertDialog(
                getString(R.string.error_message_title),
                message
            )
        }
    }
    fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.error_message_negative), null)
            .show()
    }
}