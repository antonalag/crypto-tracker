package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
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

class ExchangeCryptocurrencyDialogFragment : DialogFragment() {

    private val viewModel by sharedViewModel<ExchangeViewModel>()
    private val dialogCryptocurrencyAdapter = ExchangeDialogCryptocurrencyAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var dialogView: View
    private lateinit var listener: ExchangeCryptocurrencyDialogListener
    private var isLoading by Delegates.notNull<Boolean>()
    private lateinit var fiatName: String

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

    // Override the Fragment.onAttach() method to instantiate the ExchangeCryptocurrencyDialogListener
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

    private fun initRequests() {
        isLoading = true
        viewModel.getCryptocurrencies(fiatName, 0, 10)
    }

    private fun initViewModelObservers() {
        with(viewModel) {
            observe(cryptocurrencyListViewDtos, ::renderCryptocurrencies)
            fail(fail, ::handleFail)
        }
    }

    private fun renderCryptocurrencies(list: List<CryptocurrencyListViewDto>) {
        if (list.isEmpty()) {
            dialogView.exchange_dialog_cryptocurency_progress_bar.visibility = View.GONE
            dialogView.exchange_dialog_empty_cryptocurrencies.visibility = View.VISIBLE
        } else {
            dialogCryptocurrencyAdapter.submitList(dialogCryptocurrencyAdapter.currentList.plus(list))
            dialogView.exchange_dialog_cryptocurency_progress_bar.visibility = View.GONE
        }
        isLoading = false
    }

    private fun handleFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                dialogView.exchange_dialog_cryptocurency_progress_bar.visibility = View.GONE
                dialogView.exchange_dialog_cryptocurrencies_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun validate(): Boolean {
        // Check if user select any elements of the list
        return dialogCryptocurrencyAdapter.selectedCryptocurrencyLisViewDto != null
    }

    private fun showInvalidateRequest() {
        val toast =
            Toast.makeText(context, "No se ha seleccionado ningún elemento", Toast.LENGTH_LONG)
        toast.show()
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