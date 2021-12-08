package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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

/**
 * Fragment that shows investment dialog
 */
class AddInvestmentDialogFragment : DialogFragment() {

    private val TAG = "AddInvestmentDialogFragment"

    private val viewModel by sharedViewModel<HomeViewModel>()
    private val dialogInvestmentAdapter = DialogInvestmentAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var dialogView: View
    private lateinit var listener: InvestmentDialogListener
    lateinit var localCryptocurrencies: List<LocalCryptocurrency>

    /**
     * Set necessary data
     */
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
            // Initialize recycler view
            layoutManager = LinearLayoutManager(activity)
            dialogView.invest_cryptocurrency_list_recyclerView.layoutManager = layoutManager
            dialogView.invest_cryptocurrency_list_recyclerView.adapter = dialogInvestmentAdapter

            // Se on click listeners
            dialogView.invest_cancel.setOnClickListener {
                listener.onAddInvestmentDialogCancelClick(this)
            }
            dialogView.invest_confirm.setOnClickListener {
                if (validate()) {
                    dialogView.saving_investment_progress_bar.visibility = View.VISIBLE
                    val totalInvested = dialogView.purchased_value.text.toString().toDouble()
                    val investment = Investment(
                        totalInvested,
                        dialogInvestmentAdapter.selectedCryptocurrency!!.id
                    )
                    viewModel.saveInvestment(investment)
                } else {
                    showInvalidateRequest()
                }
            }

            // Avoid resizing dialog
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

            initViewModelObservers()

            initRequests()

            // Create
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Override the Fragment.onAttach() method to instantiate the InvestmentDialogListener
     */
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

    /**
     * Initializes the necessary requests
     */
    private fun initRequests() {
        dialogInvestmentAdapter.submitList(localCryptocurrencies)
        dialogView.invest_cryptocurency_progress_bar.visibility = View.GONE
    }

    /**
     * Configure the ExchangeViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(saveInvestment, ::callConfirmListener)
            fail(fail, ::handleFail)
        }
    }

    /**
     * It's called when the request for save investment is successful
     */
    private fun callConfirmListener(investmentId: Long) {
        // Send the confirm button event back to the host activity
        listener.onAddInvestmentDialogConfirmClick(this)
    }

    /**
     * It's called when the request cryptocurrencies information has failed
     */
    private fun handleFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.investment_request_failed))
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                showLoading(false)
                showSavingLoading(false)
                showError(true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showLoading(false)
                showSavingLoading(false)
                showError(true)
            }
        }
    }

    /**
     * Show loading
     */
    private fun showLoading(show: Boolean) {
        dialogView.investment_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Show saving loading
     */
    private fun showSavingLoading(show: Boolean) {
        dialogView.saving_investment_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Show error
     */
    private fun showError(show: Boolean) {
        dialogView.investment_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Validate user selection
     */
    private fun validate(): Boolean {
        return dialogInvestmentAdapter.selectedCryptocurrency != null && dialogView.purchased_value.text.isNotEmpty()
    }

    /**
     * A message is displayed to the user informing him/her to select a cryptocurrency and enter a quantity.
     */
    private fun showInvalidateRequest() {
        Toast.makeText(
            context,
            resources.getString(R.string.investment_dialog_invalidate_message),
            Toast.LENGTH_LONG
        ).show()
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