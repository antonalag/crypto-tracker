package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import kotlinx.android.synthetic.main.dialog_exchange_fiat.view.*
import java.io.Serializable
import java.lang.ClassCastException
import java.lang.IllegalStateException

class ExchangeFiatDialogFragment: DialogFragment() {

    private val dialogFiatAdapter = ExchangeFiatDialogAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var dialogView: View
    private lateinit var listener: ExchangeFiatDialogListener
    private lateinit var fiats: List<Fiat>

    private fun setFiats() {
        val jsonData = arguments?.getString("data") ?: ""
        val itemType = object: TypeToken<List<Fiat>>() {}.type
        fiats = if(jsonData.isNullOrEmpty()) emptyList() else Gson().fromJson<List<Fiat>>(jsonData, itemType)
    }

    /**
     * The activity that creates an instance of tihs dialog fragment must
     * implement this interface in order to receive event callbacks. Each
     * method passes the DialogFragment in case the host needs to query it
     */
    interface ExchangeFiatDialogListener {
        fun onExchangeFiatDialogCancelClick()
        fun onExchangeFiatDialogConfirmClick(fiat: Fiat)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setFiats()
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get layout inflater
            val inflater = requireActivity().layoutInflater
            //Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            dialogView = inflater.inflate(R.layout.dialog_exchange_fiat, null)
            builder.setView(dialogView)
            layoutManager = LinearLayoutManager(activity)
            dialogView.exchange_dialog_fiat_list_recyclerView.layoutManager = layoutManager
            dialogView.exchange_dialog_fiat_list_recyclerView.adapter = dialogFiatAdapter
            dialogFiatAdapter.submitList(fiats)
            // Hide progress bar
            dialogView.exchange_dialog_fiat_progress_bar.visibility = View.GONE

            // Set on click listeners
            dialogView.exchange_fiat_dialog_cancel.setOnClickListener {
                listener.onExchangeFiatDialogCancelClick()
            }

            dialogView.exchange_fiat_dialog_confirm.setOnClickListener {
                if(validate()) {
                    listener.onExchangeFiatDialogConfirmClick(dialogFiatAdapter.fiatSelected)
                } else {
                    showInvalidateRequest()
                }
            }

            // Create
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the ExchangeFiatDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the ExchangeFiatDialogListener to be able to send events to the host
            listener = context as ExchangeFiatDialogListener
        } catch (e: ClassCastException) {
            // The activity does not implement the interface, thrwo exception
            throw ClassCastException("$context must impement ExchangeFiatDialogListener")
        }
    }

    private fun validate(): Boolean {
        // Check if user select any elements of the list
        return dialogFiatAdapter.fiatSelected != null
    }

    private fun showInvalidateRequest() {
        val toast =
            Toast.makeText(context, "No se ha seleccionado ningún elemento", Toast.LENGTH_LONG)
        toast.show()
    }

    companion object {
        fun newInstance(jsonData: String?): ExchangeFiatDialogFragment {
            val fragment = ExchangeFiatDialogFragment()
            val arguments = Bundle().apply {
                putSerializable("data", jsonData)
            }
            fragment.arguments = arguments
            return fragment
        }
    }
}