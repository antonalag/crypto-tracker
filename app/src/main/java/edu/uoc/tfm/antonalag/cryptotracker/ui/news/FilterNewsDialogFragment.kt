package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import kotlinx.android.synthetic.main.dialog_news.*
import kotlinx.android.synthetic.main.dialog_news.view.*
import java.io.Serializable
import java.lang.ClassCastException

class FilterNewsDialogFragment : DialogFragment() {

    private val TAG = "FilterNewsDialogFragment"

    private val dialogNewsAdapter = DialogNewsAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var dialogView: View
    private lateinit var listener: FilterNewsDialogListener
    lateinit var localCryptocurrencies: List<LocalCryptocurrency>
    lateinit var localCryptocurrencySelected: LocalCryptocurrency
    var sortValue: String = ""

    private fun setLocalCryptocurrencies() {
        localCryptocurrencies = arguments?.getSerializable("data") as List<LocalCryptocurrency>
    }


    /**
     * The activity that creates an instance of tihs dialog fragment must
     * implement this interface in order to receive event callbacks. Each
     * method passes the DialogFragment in case the host needs to query it
     */
    interface FilterNewsDialogListener {
        fun onFilterNewsDialogConfirmClick(dialog: DialogFragment)
        fun onFilterNewsDialogCancelClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setLocalCryptocurrencies()

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get layout inflater
            val inflater = requireActivity().layoutInflater
            //Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            dialogView = inflater.inflate(R.layout.dialog_news, null)
            builder.setView(dialogView)
            layoutManager = LinearLayoutManager(activity)
            dialogView.cryptocurrency_news_list_recyclerView.layoutManager = layoutManager
            dialogView.cryptocurrency_news_list_recyclerView.adapter = dialogNewsAdapter
            dialogNewsAdapter.submitList(localCryptocurrencies)

            dialogView.cancel.setOnClickListener {
                listener.onFilterNewsDialogCancelClick(this)
            }

            dialogView.confirm.setOnClickListener {
                if (validate()) {
                    localCryptocurrencySelected =
                        dialogNewsAdapter.selectedCryptocurrency!!
                    listener.onFilterNewsDialogConfirmClick(this)
                } else {
                    showInvalidateRequest()
                }
            }


            builder.create()
        } ?: throw IllegalStateException("Acitivty cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the FilterNewsDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the InvestmentDialogListener to be able to send events to the host
            listener = context as FilterNewsDialogFragment.FilterNewsDialogListener
        } catch (e: ClassCastException) {
            // The activity does not implement the interface, thrwo exception
            throw ClassCastException("$context must impement InvestmentDialogListener")
        }
    }

    private fun validate(): Boolean {
        return try{

            !sortValue.isNullOrEmpty() &&
                    dialogNewsAdapter.selectedCryptocurrency != null
        } catch (e: Throwable) {
            Log.e(TAG, "Error validataing data: ${e.message}")
            return false
        }
    }

    private fun showInvalidateRequest() {
        val toast = Toast.makeText(
            context,
            resources.getString(R.string.dialog_news_fragment_error),
            Toast.LENGTH_LONG
        )
        toast.show()
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.id) {
                R.id.radiobutton_rising ->
                    if (checked) {
                        sortValue = "rising"
                    }
                R.id.radiobutton_hot ->
                    if (checked) {
                        sortValue = "hot"
                    }
                R.id.radiobutton_important -> {
                    if (checked) {
                        sortValue = "important"
                    }
                }
                R.id.radiobutton_lol -> {
                    if (checked) {
                        sortValue = "lol"
                    }
                }
            }
        }
    }

    companion object {

        fun newInstance(data: List<LocalCryptocurrency>?): FilterNewsDialogFragment {
            val fragment = FilterNewsDialogFragment()
            val arguments = Bundle().apply {
                putSerializable(
                    "data", data as Serializable
                )
            }
            fragment.arguments = arguments
            return fragment
        }

    }
}