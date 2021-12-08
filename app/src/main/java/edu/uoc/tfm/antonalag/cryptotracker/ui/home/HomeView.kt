package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.core.util.DateUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyCardViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.InvestmentCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.model.Quote
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.ui.news.NewsView
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.MenuFragment
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.OnSwipeTouchListener
import io.ktor.utils.io.*
import kotlinx.android.synthetic.main.activity_home_view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Class responsible to displaying resumed information about cryptocurrencies and investments
 */
class HomeView : BaseActivity(), AddCryptocurrencyDialogFragment.CryptocurrencyDialogListener,
    AddInvestmentDialogFragment.InvestmentDialogListener,
    HomeInvestmentAdapter.HomeInvestmentAdapterClickListener,
    EditInvestmentDialogFragment.EditInvestmentDialogListener,
    HomeCryptocurrencyAdapter.HomeCryptocurrencyAdapterClickListener {

    private val TAG = "HomeView"

    private lateinit var cryptocurrencyAdapter: HomeCryptocurrencyAdapter
    private lateinit var investmentAdapter: HomeInvestmentAdapter
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var addCryptocurrencyDialog: AddCryptocurrencyDialogFragment
    private lateinit var addInvestmentDialog: AddInvestmentDialogFragment
    private lateinit var editInvestmentDialog: EditInvestmentDialogFragment
    private lateinit var localCryptocurrencies: List<LocalCryptocurrency>
    private lateinit var investments: List<Investment>
    private val user: User = CryptoTrackerApp.instance.user
    private var userPreferences: UserPreferences = CryptoTrackerApp.instance.userPreferences
    private val menuOptionsDialog: MenuFragment = MenuFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.main_toolbar, R.id.main_toolbar)
        initSetLayout(R.layout.activity_home_view)
        initUI()
        initViewModelObservers()
        initRequests()
    }

    override fun onResume() {
        super.onResume()
        if(userPreferences.timeInterval != CryptoTrackerApp.instance.userPreferences.timeInterval || userPreferences.fiat != CryptoTrackerApp.instance.userPreferences.fiat) {
            userPreferences = CryptoTrackerApp.instance.userPreferences
            showView(cryptocurency_progress_bar, true)
            viewModel.getLocalCryptocurrencies(user.id)
        }
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        // Set toolbar title
        main_toolbar_title.text = resources.getString(R.string.app_name)
        // Set menu button listener
        menu_options_button.setOnClickListener {
            showMenuDialog()
        }
        // Add swipe listener to layout
        val homeViewLayout = findViewById<View>(R.id.home_view_id)
        homeViewLayout.setOnTouchListener(object : OnSwipeTouchListener(this){
            override fun onSwipeLeft() {
                startActivity(Intent(context, NewsView::class.java))
                overridePendingTransition(R.anim.slide_right, R.anim.slide_out_left)
            }
        })

        // Assign layoutManagers to recyclerviews
        cryptocurrency_card_list_recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        investment_list_recyclerView.layoutManager = LinearLayoutManager(this)
        // Set adapters
        cryptocurrencyAdapter = HomeCryptocurrencyAdapter(this)
        cryptocurrency_card_list_recyclerView.adapter = cryptocurrencyAdapter
        investmentAdapter = HomeInvestmentAdapter(this)
        investment_list_recyclerView.adapter = investmentAdapter
        // Assign on event clicks
        add_cryptocurrency.setOnClickListener {
            showCryptocurrencyDialog()
        }
        add_investment.setOnClickListener {
            showInvestmentDialog()
        }
        // Set buttons disable until data is requested
        enableAddCryptocurrencyButton(false)
        enableInvestmentButton(false)
    }

    /**
     * Enable click action in add cryptocurrency button
     */
    private fun enableAddCryptocurrencyButton(disabled: Boolean) {
        add_cryptocurrency.isClickable = disabled
    }

    /**
     * Enable click action in add investment button
     */
    private fun enableInvestmentButton(disabled: Boolean) {
        add_investment.isClickable = disabled
    }

    /**
     * Initializes the necessary requests
     */
    private fun initRequests() {
        // Get stored cryptocurrencies
        viewModel.getLocalCryptocurrencies(user.id)
        // Get quote
        viewModel.getPhraseOfTheDay()
    }

    /**
     * Configure the HomeViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(quote, ::handleQuoteSuccess)
            fail(fail, ::handleQuoteFail)
        }

        with(viewModel) {
            observe(localCryptocurrencies, ::handleLocalCryptocurrencySuccess)
            fail(fail, ::handleCryptocurrencyFail)
        }

        with(viewModel) {
            observe(cryptocurrencyCardViewDtoList, ::handleCryptocurrencyCardViewDtoListSuccess)
            fail(fail, ::handleCryptocurrencyFail)
        }

        with(viewModel) {
            observe(investments, ::handleInvestmentsSuccess)
            fail(fail, ::handleInvestmentsFail)
        }

        with(viewModel) {
            observe(isInvestmentDeleted, ::handleIsInvestmentDeletedSuccess)
            fail(fail, ::handleInvestmentsFail)
        }

        with(viewModel) {
            observe(isInvestmentUpdated, ::handleIsInvestmentUpdatedSuccess)
            fail(fail, ::handleInvestmentsFail)
        }

        with(viewModel) {
            observe(isLocalCryptocurrencyDeleted, ::handleIsLocalCryptocurrencyDeletedSuccess)
            fail(fail, ::handleCryptocurrencyFail)
        }
    }

    /**
     * It's called when the request for quote is successful
     */
    private fun handleQuoteSuccess(phrase: Quote) {
        Log.v(TAG, resources.getString(R.string.home_view_quote_request_successful))
        // Set user name and surmane
        greeting.text = DateUtil.getTimeOfDayMessage() + user.name + " " + user.surname
        // Set quote
        quote.text = phrase.quote
        // Set author
        author.text = phrase.author
        // hide quote progress bar
        showView(quote_progress_bar, false)
    }

    /**
     * It's called when the request for quote has failed
     */
    private fun handleQuoteFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.home_view_quote_request_failed))
        when (fail) {
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showView(quote_progress_bar, false)
                showView(quote_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(quote_progress_bar, false)
                showView(quote_error, true)
            }
        }
    }

    /**
     * It's called when the request for cryptocurrencies stored locally  are successful
     */
    private fun handleLocalCryptocurrencySuccess(list: List<LocalCryptocurrency>) {
        Log.v(TAG, resources.getString(R.string.local_cryptocurrencies_request_succesful))
        localCryptocurrencies = list
        // Set addCryptocurrency dialog
        /*if (::addCryptocurrencyDialog.isInitialized && addCryptocurrencyDialog.isAdded) {
            addCryptocurrencyDialog.dismiss()
        }*/
        addCryptocurrencyDialog = AddCryptocurrencyDialogFragment.newInstance(localCryptocurrencies)
        // set addinvestment dialog
        /*if (::addInvestmentDialog.isInitialized && addInvestmentDialog.isAdded) {
            addInvestmentDialog.dismiss()
        }*/
        addInvestmentDialog = AddInvestmentDialogFragment.newInstance(localCryptocurrencies)

        if (localCryptocurrencies.isEmpty()) {
            Log.v(TAG, resources.getString(R.string.local_cryptocurrencies_request_not_found))
            showView(cryptocurency_progress_bar, false)
            showView(empty_cryptocurrencies, true)
            enableAddCryptocurrencyButton(true)
            enableInvestmentButton(false)
            showView(investment_progress_bar, false)
            showView(empty_investments, true)
        } else {
            // Set data on adapter
            cryptocurrencyAdapter.savedCryptocurrencies = localCryptocurrencies
            // Get investments
            viewModel.getInvestments(localCryptocurrencies.map { it.id })
            // Get card view data
            viewModel.getCryptocurrencyCardViewDtoList(
                localCryptocurrencies.map { it.name.toLowerCase() },
                userPreferences.fiat,
                userPreferences.timeInterval
            )
        }
    }
    /**
     * It's called when the requests for cryptocurrencies have failed
     */
    private fun handleCryptocurrencyFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.cryptocurrency_request_failed))
        when (fail) {
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showView(cryptocurency_progress_bar, false)
                showView(cryptocurrencies_error, true)
            }
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                showView(cryptocurency_progress_bar, false)
                showView(cryptocurrencies_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(cryptocurency_progress_bar, false)
                showView(cryptocurrencies_error, true)
            }
        }
    }

    /**
     * It's called when the request for cryptocurrencies card view is successful
     */
    private fun handleCryptocurrencyCardViewDtoListSuccess(list: List<CryptocurrencyCardViewDto>) {
        if (list.isEmpty()) {
            Log.v(TAG, resources.getString(R.string.cryptocurrency_request_not_found))
            showView(cryptocurency_progress_bar, false)
            showView(empty_cryptocurrencies, true)
        } else {
            Log.v(TAG, resources.getString(R.string.cryptocurrency_request_successful))
            // Submit list to adapter
            cryptocurrencyAdapter.submitList(list)
            showView(cryptocurency_progress_bar, false)
            showView(empty_cryptocurrencies, false)
        }
        // Enable add cryptocurrency button
        enableAddCryptocurrencyButton(true)
    }

    /**
     * It's called when the request for investments is successful
     */
    private fun handleInvestmentsSuccess(investmentList: List<Investment>) {
        if (investmentList.isEmpty()) {
            Log.v(TAG, resources.getString(R.string.investment_request_not_found))
            showView(investment_progress_bar, false)
            showView(empty_investments, true)
        } else {
            Log.v(TAG, resources.getString(R.string.investment_request_successful))
            // Set investments
            investments = investmentList
            // Get InvestmentCryptocurrencies entities from investments groupe by local cryptocurrency
            val localCryptocurrencyById = localCryptocurrencies.associateBy { it.id }
            val investmentCryptocurrency: List<InvestmentCryptocurrency> =
                investmentList.filter { localCryptocurrencyById[it.cryptocurrencyId] != null }
                    .mapNotNull { investment ->
                        localCryptocurrencyById[investment.cryptocurrencyId]?.let { localCryptocurrency ->
                            InvestmentCryptocurrency(
                                localCryptocurrency.name,
                                localCryptocurrency.symbol,
                                localCryptocurrency.icon,
                                localCryptocurrency.price,
                                localCryptocurrency.id,
                                investment.totalInvested
                            )
                        }
                    }
            // Submit list to adapter
            investmentAdapter.submitList(investmentCryptocurrency)
            showView(investment_progress_bar, false)
            showView(empty_investments, false)
        }
        // Enable investment button
        enableInvestmentButton(true)
    }

    /**
     * It's called when the request for investments has failed
     */
    private fun handleInvestmentsFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.investment_request_failed))
        when (fail) {
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showView(investment_progress_bar, false)
                showView(investment_error, true)
            }
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                showView(investment_progress_bar, false)
                showView(investment_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(investment_progress_bar, false)
                showView(investment_error, true)
            }
        }
    }

    /**
     * It's called when the request for delete an investment is successful
     */
    private fun handleIsInvestmentDeletedSuccess(isDeleted: Boolean) {
        if (!isDeleted) {
            Log.v(TAG, resources.getString(R.string.investment_delete_request_failed))
            Toast.makeText(
                this,
                resources.getString(R.string.delete_investment_error),
                Toast.LENGTH_SHORT
            ).show()
        }
        // Get investments
        viewModel.getInvestments(localCryptocurrencies.map { it.id })

    }

    /**
     * It's called when the request for update an investment is successful
     */
    private fun handleIsInvestmentUpdatedSuccess(isUpdated: Boolean) {
        if (!isUpdated) {
            Log.v(TAG, resources.getString(R.string.investment_update_request_failed))
            Toast.makeText(
                this,
                resources.getString(R.string.edit_investment_error),
                Toast.LENGTH_SHORT
            ).show()
        }
        // Get investments
        viewModel.getInvestments(localCryptocurrencies.map { it.id })
    }

    /**
     * It's called when the request for delete a local cryptocurrency is successful
     */
    private fun handleIsLocalCryptocurrencyDeletedSuccess(isDeleted: Boolean) {
        if(!isDeleted) {
            Log.v(TAG, resources.getString(R.string.local_cryptocurrency_delete_request_failed))
            Toast.makeText(this, resources.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
            showView(cryptocurency_progress_bar, false)
        } else {
            // Get local cryptocurrencies
            viewModel.getLocalCryptocurrencies(user.id)
        }
    }

    /**
     * Show Cryptocurrency dialog
     */
    private fun showCryptocurrencyDialog() {
        if (!addCryptocurrencyDialog.isAdded) {
            addCryptocurrencyDialog.show(supportFragmentManager, "AddCryptocurrencyDialogFragment")
        }
    }

    /**
     * Callback function that detect when the user clicks on AddCryptocurrencyDialog confirm button
     */
    override fun onAddCryptocurrencyDialogConfirmClick(dialog: DialogFragment) {
        // Close dialog
        addCryptocurrencyDialog.dismiss()
        // Update cryptocurrencies
        showView(cryptocurency_progress_bar, true)
        // Get local cryptocurrencies
        viewModel.getLocalCryptocurrencies(user.id)
    }

    /**
     * Callback function that detect when the user clicks on AddCryptocurrencyDialog cancel button
     */
    override fun onAddCryptocurrencyDialogCancelClick(dialog: DialogFragment) {
        // Close dialog
        addCryptocurrencyDialog.dismiss()
    }

    /**
     * Show investment dialog
     */
    private fun showInvestmentDialog() {
        if (!addInvestmentDialog.isAdded) {
            addInvestmentDialog.show(supportFragmentManager, "AddInvestmentDialogFragment")
        }

    }

    /**
     * Callback function that detect when the user clicks on AddInvestmentDialog confirm button
     */
    override fun onAddInvestmentDialogConfirmClick(dialog: DialogFragment) {
        // Close dialog
        addInvestmentDialog.dismiss()
        // Update investment
        viewModel.getInvestments(localCryptocurrencies.map { it.id })
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    /**
     * Callback function that detect when the user clicks on AddInvestmentDialog cancel button
     */
    override fun onAddInvestmentDialogCancelClick(dialog: DialogFragment) {
        // Close dialog
        addInvestmentDialog.dismiss()
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    /**
     * Show menu dialog
     */
    private fun showMenuDialog() {
        if (!menuOptionsDialog.isAdded) {
            menuOptionsDialog.show(supportFragmentManager, "MenuOptionsDialog")
        }
    }

    /**
     * Callback function that detect when the user click on delete button showed in investment recycler view
     * when user swiped on items
     */
    override fun onDeleteInvestmentClickListener(localCryptocurrencyId: Long) {
        showView(investment_progress_bar, true)
        // Get investment selected
        val investment = investments.first { it.cryptocurrencyId == localCryptocurrencyId }
        // Delete investment
        viewModel.deleteInvestment(investment.id)
    }

    /**
     * Callback function that detect when the user click on edit button showed in investment recycler view
     * when user swiped on items
     */
    override fun onEditInvestmentClickListener(localCryptocurrencyId: Long) {
        // Get cryptocurrency name
        val cryptocurrencyName = localCryptocurrencies.first { it.id == localCryptocurrencyId }.name
        // Get toal purchased
        val totalPurchased =
            investments.first { it.cryptocurrencyId == localCryptocurrencyId }.totalInvested
        // instance EditInvestment Dialog
        editInvestmentDialog = EditInvestmentDialogFragment.newInstance(
            localCryptocurrencyId,
            cryptocurrencyName,
            totalPurchased
        )
        // Show dialog
        editInvestmentDialog.show(supportFragmentManager, "EditInvestmentDialogFragment")
    }

    /**
     * Callback function that detect when the user clicks on EditInvestmentDialog cancel button
     */
    override fun onCancelClick() {
        // Close dialog
        editInvestmentDialog.dismiss()
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    /**
     * Callback function that detect when the user clicks on AddInvestmentDialog confirm button
     */
    override fun onConfirmEditClick(localCryptocurrencyId: Long, purchasedValue: Double) {
        // Get investment
        val actualInvestment = investments.first { it.cryptocurrencyId == localCryptocurrencyId }
        val investmentEdited = Investment(
            actualInvestment.id,
            purchasedValue,
            localCryptocurrencyId
        )
        showView(investment_progress_bar, true)
        // Show dialog
        editInvestmentDialog.dismiss()
        // Update investments
        viewModel.updateInvestment(investmentEdited)
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    /**
     * Callback function that detect when the user clicks on Delete button showed in local cryptocurrency card view
     */
    override fun onDeleteCryptocurrencyClickListener(localCryptocurrencyId: Long) {
        showView(cryptocurency_progress_bar, true)
        // Delete local cryptocurrency
        viewModel.deleteLocalCryptocurrency(localCryptocurrencyId)
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    /**
     * Callback function that detect when the user clicks on refresh button showed in local cryptocurrency card view
     */
    override fun onRefresCryptocurrencyClickListener(localCryptocurrencyId: Long) {
        showView(cryptocurency_progress_bar, true)
        // Get local cryptocurrencies
        viewModel.getLocalCryptocurrencies(user.id)
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

}