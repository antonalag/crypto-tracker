package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.roundToString
import kotlinx.android.synthetic.main.dialog_edit_investment.view.*
import java.lang.ClassCastException
import java.lang.IllegalStateException
import kotlin.properties.Delegates

class EditInvestmentDialogFragment : DialogFragment() {

    private lateinit var dialogView: View
    private lateinit var listener: EditInvestmentDialogListener
    private lateinit var cryptocurencyName: String
    private var purchasedValue by Delegates.notNull<Double>()
    private var cryptocurrencyId by Delegates.notNull<Long>()

    /**
     * The activity that creates an instance of tihs dialog fragment must
     * implement this interface in order to receive event callbacks. Each
     * method passes the DialogFragment in case the host needs to query it
     */
    interface EditInvestmentDialogListener {
        fun onCancelClick()
        fun onConfirmEditClick(cryptocurrencyId: Long, purchasedValue: Double)
    }

    private fun setData() {
        cryptocurencyName = arguments?.getString("cryptocurrencyName", "") ?: ""
        purchasedValue = arguments?.getDouble("purchasedValue") ?: 0.0
        cryptocurrencyId = arguments?.getLong("cryptocurrencyId") ?: 0L
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setData()

        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            // Get layout inflater
            val inflater = requireActivity().layoutInflater
            //Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            dialogView = inflater.inflate(R.layout.dialog_edit_investment, null)
            builder.setView(dialogView)
            dialogView.edit_investment_cryptocurrency_name.text = cryptocurencyName
            dialogView.edit_investment_purchased_value.setText(purchasedValue.roundToString(2))
            // Set on click listeners
            dialogView.edit_invest_cancel.setOnClickListener {
                listener.onCancelClick()
            }
            dialogView.edit_invest_confirm.setOnClickListener {
                if(dialogView.edit_investment_purchased_value.text.isNullOrEmpty()) {
                    Toast.makeText(context, resources.getString(R.string.edit_investment_purchase_validation), Toast.LENGTH_SHORT).show()
                } else {
                    val newPurchasedValue = dialogView.edit_investment_purchased_value.text.toString().toDouble()
                    listener.onConfirmEditClick(cryptocurrencyId, newPurchasedValue)
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the EditInvestmentDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the EditInvestmentDialogListener to be able to send events to the host
            listener = context as EditInvestmentDialogFragment.EditInvestmentDialogListener
        } catch (e: ClassCastException) {
            // The activity does not implement the interface, thrwo exception
            throw ClassCastException("$context must impement EditInvestmentDialogListener")
        }
    }


    companion object {

        fun newInstance(
            cryptocurrencyId: Long,
            cryptocurrencyName: String,
            purchasedValue: Double
        ): EditInvestmentDialogFragment {
            val fragment = EditInvestmentDialogFragment()
            val arguments = Bundle().apply {
                putString("cryptocurrencyName", cryptocurrencyName)
                putDouble("purchasedValue", purchasedValue)
                putLong("cryptocurrencyId", cryptocurrencyId)
            }
            fragment.arguments = arguments
            return fragment
        }
    }

}