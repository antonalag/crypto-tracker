package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment
import kotlinx.android.synthetic.main.activity_home_view.view.*
import kotlinx.android.synthetic.main.dialog_investment.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.Serializable
import java.lang.ClassCastException
import java.lang.IllegalStateException

class AddInvestmentDialogFragment: DialogFragment() {

    private val viewModel by sharedViewModel<HomeViewModel>()
    private val dialogInvestmentAdapter = DialogInvestmentAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var dialogView: View
    private lateinit var listener: InvestmentDialogListener
    lateinit var localCryptocurrencies: List<LocalCryptocurrency>

    private fun setLocalCryptocurrencies() {
        localCryptocurrencies = arguments?.getSerializable("data") as List<LocalCryptocurrency>
    }

    /**
     * The activity that creates an instance of tihs dialog fragment must
     * implement this interface in order to receive event callbacks. Each
     * method passes the DialogFragment in case the host needs to query it
     */
    interface InvestmentDialogListener {
        fun onAddInvestmentDialogConfirmClick(dialog: DialogFragment)
        fun onAddInvestmentDialogCancelClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setLocalCryptocurrencies()

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get layout inflater
            val inflater = requireActivity().layoutInflater
            //Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            dialogView = inflater.inflate(R.layout.dialog_investment, null)
            builder.setView(dialogView)
            layoutManager = LinearLayoutManager(activity)
            dialogView.invest_cryptocurrency_list_recyclerView.layoutManager = layoutManager
            dialogView.invest_cryptocurrency_list_recyclerView.adapter = dialogInvestmentAdapter

            dialogView.invest_cancel.setOnClickListener {
                listener.onAddInvestmentDialogCancelClick(this)
            }

            dialogView.invest_confirm.setOnClickListener {
                if(validate()) {
                    dialogView.saving_investment_progress_bar.visibility = View.VISIBLE
                    val totalInvested = dialogView.purchased_value.text.toString().toDouble()
                    val investment = Investment(totalInvested, dialogInvestmentAdapter.selectedCryptocurrency!!.id)
                    viewModel.saveInvestment(investment)
                } else {
                    showInvalidateRequest()
                }
            }

            // Avoid resizing dialog
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

            initViewModelObservers()

            initRequests()

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the InvestmentDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the InvestmentDialogListener to be able to send events to the host
            listener = context as InvestmentDialogListener
        } catch (e: ClassCastException) {
            // The activity does not implement the interface, thrwo exception
            throw ClassCastException("$context must impement InvestmentDialogListener")
        }
    }

    private fun initRequests() {
        dialogInvestmentAdapter.submitList(localCryptocurrencies)
        dialogView.invest_cryptocurency_progress_bar.visibility = View.GONE
    }

    private fun initViewModelObservers() {
        with(viewModel) {
            observe(saveInvestment, ::callConfirmListener)
            fail(fail, ::handleFail)
        }
    }

    private fun callConfirmListener(investmentId: Long) {
        // Send the confirm button event back to the host activity
        listener.onAddInvestmentDialogConfirmClick(this)
    }

    private fun handleFail(fail: Fail) {
        when(fail) {
            is Fail.LocalFail -> {
                dialogView.investment_progress_bar.visibility = View.GONE
                dialogView.saving_investment_progress_bar.visibility = View.GONE
                dialogView.investment_error.visibility = View.VISIBLE
            } else -> {
                // Nothing to do
            }
        }
    }

    private fun validate(): Boolean {
        return dialogInvestmentAdapter.selectedCryptocurrency != null && dialogView.purchased_value.text.isNotEmpty()
    }

    private fun showInvalidateRequest() {
        val toast = Toast.makeText(context, "Debe selecciona una criptomoneda y añadir un valor adquirido", Toast.LENGTH_LONG)
        toast.show()
    }

    companion object {

        fun newInstance(data: List<LocalCryptocurrency>?): AddInvestmentDialogFragment {
            val fragment = AddInvestmentDialogFragment()
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