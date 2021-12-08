package edu.uoc.tfm.antonalag.cryptotracker.ui.user

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import edu.uoc.tfm.antonalag.cryptotracker.R
import kotlinx.android.synthetic.main.dialog_confirm_delete_account.*
import kotlinx.android.synthetic.main.dialog_confirm_delete_account.view.*
import java.lang.ClassCastException
import java.lang.IllegalStateException

/**
 * Fragment that shows delete account dialog
 */
class DeleteUserAccountDialogFragment: DialogFragment() {

    private lateinit var listener: DeleteUserAccountDialogListener

    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks. Each
     * method passes the DialogFragment in case the host needs to query it
     */
    interface DeleteUserAccountDialogListener {
        fun onDialogConfirmClick(dialog: DialogFragment)
        fun onDialogCancelClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get layout inflater
            val inflater = requireActivity().layoutInflater
            //Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val dialogView = inflater.inflate(R.layout.dialog_confirm_delete_account, null)
            builder.setView(dialogView)
            // Set listeners
            dialogView.cancel_delete_account_button.setOnClickListener {
                listener.onDialogCancelClick(this)
            }
            dialogView.confirm_delete_account_button.setOnClickListener {
                listener.onDialogConfirmClick(this)
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Override the Fragment.onAttach() method to instantiate the DeleteUserAccountDialogListener
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the DeleteUserAccountDialogListener to be able to send events to the host
            listener = context as DeleteUserAccountDialogFragment.DeleteUserAccountDialogListener
        } catch (e: ClassCastException) {
            // The activity does not implement the interface, thrwo exception
            throw ClassCastException("$context must impement DeleteUserAccountDialogListener")
        }
    }

}