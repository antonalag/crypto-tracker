package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.PaginationScrollListener
import kotlinx.android.synthetic.main.dialog_cryptocurrency.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.Serializable
import java.lang.ClassCastException
import java.lang.IllegalStateException
import kotlin.properties.Delegates

class AddCryptocurrencyDialogFragment : DialogFragment() {

    private val viewModel by sharedViewModel<HomeViewModel>()
    private val dialogCryptocurrencyAdapter = DialogCryptocurrencyAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var dialogView: View
    private var isLoading by Delegates.notNull<Boolean>()
    private lateinit var listener: CryptocurrencyDialogListener
    lateinit var savedCryptocurrencies: List<LocalCryptocurrency>
    private val userPreferences: UserPreferences = CryptoTrackerApp.instance.userPreferences

    private fun setSavedCryptocurrencies() {
        savedCryptocurrencies =
            arguments?.getSerializable("data") as List<LocalCryptocurrency>
    }


    /**
     * The activity that creates an instance of tihs dialog fragment must
     * implement this interface in order to receive event callbacks. Each
     * method passes the DialogFragment in case the host needs to query it
     */
    interface CryptocurrencyDialogListener {
        fun onAddCryptocurrencyDialogConfirmClick(dialog: DialogFragment)
        fun onAddCryptocurrencyDialogCancelClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Set saved Cryptocurrencies
        setSavedCryptocurrencies()

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get layout inflater
            val inflater = requireActivity().layoutInflater
            //Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            dialogView = inflater.inflate(R.layout.dialog_cryptocurrency, null)
            builder.setView(dialogView)
            layoutManager = LinearLayoutManager(activity)
            dialogView.cryptocurrency_list_recyclerView.layoutManager = layoutManager
            // Set saved cryptocurrency in order to inform the adapter of cryptocurrencies
            // that are already stored so that the user cannot add it again
            dialogCryptocurrencyAdapter.savedCryptocurrencies = savedCryptocurrencies
            dialogView.cryptocurrency_list_recyclerView.adapter = dialogCryptocurrencyAdapter
            // Set Pagination listener
            dialogView.cryptocurrency_list_recyclerView.addOnScrollListener(object :
                PaginationScrollListener(layoutManager) {

                override fun loadMoreItems() {
                    this@AddCryptocurrencyDialogFragment.isLoading = true
                    viewModel.getCryptocurrencytListViewDtoList(userPreferences.fiat, layoutManager.itemCount, 10)
                }

                override val isLastPage: Boolean
                    get() = false

                override val isLoading: Boolean
                    get() = this@AddCryptocurrencyDialogFragment.isLoading

            })

            dialogView.cancel.setOnClickListener {
                listener.onAddCryptocurrencyDialogCancelClick(this)
            }

            dialogView.confirm.setOnClickListener {
                if(validate()) {
                    dialogView.saving_cryptocurency_progress_bar.visibility = View.VISIBLE
                    viewModel.saveCryptocurrencies(dialogCryptocurrencyAdapter.selectedCryptocurrencies)
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

    // Override the Fragment.onAttach() method to instantiate the CryptocurrencyDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the CryptocurrencyDialogListener to be able to send events to the host
            listener = context as CryptocurrencyDialogListener
        } catch (e: ClassCastException) {
            // The activity does not implement the interface, thrwo exception
            throw ClassCastException("$context must impement CryptocurrencyDialogListener")
        }
    }

    private fun initRequests() {
        isLoading = true
        viewModel.getCryptocurrencytListViewDtoList(userPreferences.fiat, 0, 10)
    }

    private fun initViewModelObservers() {
        with(viewModel) {
            observe(cryptocurrencyListViewDtoList, ::renderCryptocurrencies)
            fail(fail, ::handleFail)
        }

        with(viewModel) {
            observe(saveCryptocurrencies, ::callConfirmListener)
            fail(fail, ::handleFail)
        }
    }

    private fun callConfirmListener(list: List<Long>){
        // Send the confirm button event back to the host activity
        listener.onAddCryptocurrencyDialogConfirmClick(this)
    }

    private fun renderCryptocurrencies(list: List<CryptocurrencyListViewDto>) {
        if (list.isEmpty()) {
            dialogView.cryptocurency_progress_bar.visibility = View.GONE
            dialogView.empty_cryptocurrencies.visibility = View.VISIBLE
        } else {
            dialogCryptocurrencyAdapter.submitList(dialogCryptocurrencyAdapter.currentList.plus(list))
            dialogView.cryptocurency_progress_bar.visibility = View.GONE
        }
        isLoading = false
    }

    private fun handleFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                dialogView.cryptocurency_progress_bar.visibility = View.GONE
                dialogView.saving_cryptocurency_progress_bar.visibility = View.GONE
                dialogView.cryptocurrencies_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun validate(): Boolean {
        // Check if user select any elements of the list
        return dialogCryptocurrencyAdapter.selectedCryptocurrencies.isNotEmpty()
    }

    private fun showInvalidateRequest() {
        val toast = Toast.makeText(context, "No se ha seleccionado ningún elemento", Toast.LENGTH_LONG)
        toast.show()
    }

    companion object {

        fun newInstance(data: List<LocalCryptocurrency>?): AddCryptocurrencyDialogFragment {
            val fragment = AddCryptocurrencyDialogFragment()
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