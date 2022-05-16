package com.danpike.socialapp.fragments

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.danpike.socialapp.R

abstract class BaseFragment : Fragment() {
    fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.error_message_negative), null)
            .show()
    }
}