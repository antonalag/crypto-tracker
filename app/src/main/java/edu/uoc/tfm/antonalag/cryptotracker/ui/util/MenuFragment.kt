package edu.uoc.tfm.antonalag.cryptotracker.ui.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.ui.exchange.ExchangeView
import edu.uoc.tfm.antonalag.cryptotracker.ui.home.HomeView
import edu.uoc.tfm.antonalag.cryptotracker.ui.news.NewsView
import edu.uoc.tfm.antonalag.cryptotracker.ui.user.UserView
import edu.uoc.tfm.antonalag.cryptotracker.ui.userpreferences.UserPreferencesView
import kotlinx.android.synthetic.main.main_toolbar_menu_options.view.*
import java.lang.IllegalStateException

class MenuFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get layout inflater
            val inflater = requireActivity().layoutInflater
            // Pass null as the parent view because its going in the dialog layout
            val dialogView = inflater.inflate(R.layout.main_toolbar_menu_options, null)
            builder.setView(dialogView)
            // Set listeners
            dialogView.home_menu_option.setOnClickListener {
                startActivity(Intent(context, HomeView::class.java))
            }
            dialogView.news_menu_option.setOnClickListener {
                startActivity(Intent(context, NewsView::class.java))
            }
            dialogView.converter_menu_option.setOnClickListener {
                startActivity(Intent(context, ExchangeView::class.java))
            }
            dialogView.user_menu_option.setOnClickListener {
                startActivity(Intent(context, UserView::class.java))
            }
            dialogView.user_preferences_menu_option.setOnClickListener {
                startActivity(Intent(context, UserPreferencesView::class.java))
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}