package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.PaginationScrollListener
import kotlinx.android.synthetic.main.dialog_cryptocurrency.view.*
import kotlinx.android.synthetic.main.dialog_exchange_cryptocurrency.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.Serializable
import java.lang.ClassCastException
import java.lang.IllegalStateException
import kotlin.properties.Delegates

/**
 * Fragment that shows Cryptocurrency dialog
 */
class ExchangeCryptocurrencyDialogFragment : DialogFragment() {

    private val TAG = "ExchangeCryptocurrencyDialogFragment"

    private val viewModel by sharedViewModel<ExchangeViewModel>()
    private val dialogCryptocurrencyAdapter = ExchangeDialogCryptocurrencyAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var dialogView: View
    private lateinit var listener: ExchangeCryptocurrencyDialogListener
    private var isLoading by Delegates.notNull<Boolean>()
    private lateinit var fiatName: String

    /**
     * Set necessary data passed as arguments
     */
    private fun setFiatName() {
        fiatName = arguments?.getString("fiatName") ?: CryptoTrackerApp.instance.userPreferences.fiat
    }

    /**
     * The activity that creates an instance of tihs dialog fragment must
     * implement this interface in order to receive event callbacks. Each
     * method passes the DialogFragment in case the host needs to query it
     */
    interface ExchangeCryptocurrencyDialogListener {
        fun onExchangeCryptocurrencyDialogCancelClick()
        fun onExchangeCryptocurrencyDialogConfirmClick(cryptocurrencyListViewDto: CryptocurrencyListViewDto)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setFiatName()
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get layout inflater
            val inflater = requireActivity().layoutInflater
            //Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            dialogView = inflater.inflate(R.layout.dialog_exchange_cryptocurrency, null)
            builder.setView(dialogView)
            // Init recylcer view
            layoutManager = LinearLayoutManager(activity)
            dialogView.exchange_dialog_cryptocurrency_list_recyclerView.layoutManager =
                layoutManager
            dialogView.exchange_dialog_cryptocurrency_list_recyclerView.adapter =
                dialogCryptocurrencyAdapter
            // Set Pagination listener
            dialogView.exchange_dialog_cryptocurrency_list_recyclerView.addOnScrollListener(object :
                PaginationScrollListener(layoutManager) {

                override fun loadMoreItems() {
                    this@ExchangeCryptocurrencyDialogFragment.isLoading = true
                    viewModel.getCryptocurrencies(fiatName, layoutManager.itemCount, 10)
                }

                override val isLastPage: Boolean
                    get() = false

                override val isLoading: Boolean
                    get() = this@ExchangeCryptocurrencyDialogFragment.isLoading

            })

            // Set on click listeners
            dialogView.exchange_dialog_cancel.setOnClickListener {
                listener.onExchangeCryptocurrencyDialogCancelClick()
            }
            dialogView.exchange_dialog_confirm.setOnClickListener {
                if (validate()) {
                    listener.onExchangeCryptocurrencyDialogConfirmClick(dialogCryptocurrencyAdapter.selectedCryptocurrencyLisViewDto!!)
                } else {
                    showInvalidateRequest()
                }
            }

            initViewModelObservers()
            initRequests()
            // Create
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Override the Fragment.onAttach() method to instantiate the ExchangeCryptocurrencyDialogListener
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the ExchangeCryptocurrencyDialogListener to be able to send events to the host
            listener = context as ExchangeCryptocurrencyDialogListener
        } catch (e: ClassCastException) {
            // The activity does not implement the interface, thrwo exception
            throw ClassCastException("$context must impement ExchangeCryptocurrencyDialogListener")
        }
    }

    /**
     * Initializes the necessary requests
     */
    private fun initRequests() {
        isLoading = true
        // Get cryptocurrencies
        viewModel.getCryptocurrencies(fiatName, 0, 10)
    }

    /**
     * Configure the ExchangeViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(cryptocurrencyListViewDtos, ::renderCryptocurrencies)
            fail(fail, ::handleFail)
        }
    }

    /**
     * It's called when the request for cryptocurrencies information is successful
     */
    private fun renderCryptocurrencies(list: List<CryptocurrencyListViewDto>) {
        if (list.isEmpty()) {
            Log.v(TAG, resources.getString(R.string.cryptocurrency_request_not_found))
            showLoading(false)
            showEmpty(true)
        } else {
            Log.v(TAG, resources.getString(R.string.cryptocurrency_request_successful))
            dialogCryptocurrencyAdapter.submitList(dialogCryptocurrencyAdapter.currentList.plus(list))
            showLoading(false)
        }
        isLoading = false
    }

    /**
     * It's called when the request cryptocurrencies information has failed
     */
    private fun handleFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.cryptocurrency_request_failed))
        when (fail) {
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showLoading(false)
                showError(true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showLoading(false)
                showError(true)
            }
        }
    }

    /**
     * Show loading
     */
    private fun showLoading(show: Boolean) {
        dialogView.exchange_dialog_cryptocurency_progress_bar.visibility = if(show) View.VISIBLE else View.GONE
    }

    /**
     * Show error
     */
    private fun showError(show: Boolean) {
        dialogView.exchange_dialog_cryptocurrencies_error.visibility = if(show) View.VISIBLE else View.GONE
    }

    /**
     * Show empty
     */
    private fun showEmpty(show: Boolean) {
        dialogView.exchange_dialog_empty_cryptocurrencies.visibility = if(show) View.VISIBLE else View.GONE
    }

    /**
     * Validate user selection
     */
    private fun validate(): Boolean {
        // Check if user select any elements of the list
        return dialogCryptocurrencyAdapter.selectedCryptocurrencyLisViewDto != null
    }

    /**
     * A message is displayed to the user informing him/her to select a cryptocurrency.
     */
    private fun showInvalidateRequest() {
        Toast.makeText(context, resources.getString(R.string.item_no_selected), Toast.LENGTH_LONG)
            .show()
    }

    companion object {
        fun newInstance(fiatName: String): ExchangeCryptocurrencyDialogFragment {
            val fragment = ExchangeCryptocurrencyDialogFragment()
            val arguments = Bundle().apply {
                putString("fiatName", fiatName)
            }
            fragment.arguments = arguments
            return fragment
        }
    }
}