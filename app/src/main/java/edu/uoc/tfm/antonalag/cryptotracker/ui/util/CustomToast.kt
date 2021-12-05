package edu.uoc.tfm.antonalag.cryptotracker.ui.util

import android.content.Context
import android.view.View
import android.widget.Toast

class CustomToast {

    companion object {

        fun error(context: Context, message: String) {
            val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        }
    }
}