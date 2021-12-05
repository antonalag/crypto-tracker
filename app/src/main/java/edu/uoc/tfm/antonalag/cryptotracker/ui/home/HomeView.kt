package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.content.Intent
import android.os.Bundle
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
    private val userPreferences: UserPreferences = CryptoTrackerApp.instance.userPreferences
    private val menuOptionsDialog: MenuFragment = MenuFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.main_toolbar, R.id.main_toolbar)
        initSetLayout(R.layout.activity_home_view)
        initUI()
        initViewModelObservers()
        initRequests()
    }

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

    private fun enableAddCryptocurrencyButton(disabled: Boolean) {
        add_cryptocurrency.isClickable = disabled
    }

    private fun enableInvestmentButton(disabled: Boolean) {
        add_investment.isClickable = disabled
    }

    private fun initRequests() {
        // Get stored cryptocurrencies
        viewModel.getLocalCryptocurrencies(user.id)
        // Get quote
        viewModel.getPhraseOfTheDay()
    }

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

    private fun handleQuoteSuccess(phrase: Quote) {
        greeting.text = DateUtil.getTimeOfDayMessage() + user.name + " " + user.surname
        quote.text = phrase.quote
        author.text = phrase.author
        quote_progress_bar.visibility = View.GONE
    }

    private fun handleQuoteFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                quote_progress_bar.visibility = View.GONE
                quote_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handleLocalCryptocurrencySuccess(list: List<LocalCryptocurrency>) {
        localCryptocurrencies = list
        // Set addCryptocurrency dialog
        if (::addCryptocurrencyDialog.isInitialized && addCryptocurrencyDialog.isAdded) {
            addCryptocurrencyDialog.dismiss()
        }
        addCryptocurrencyDialog = AddCryptocurrencyDialogFragment.newInstance(localCryptocurrencies)
        // set addinvestment dialog
        if (::addInvestmentDialog.isInitialized && addInvestmentDialog.isAdded) {
            addInvestmentDialog.dismiss()
        }
        addInvestmentDialog = AddInvestmentDialogFragment.newInstance(localCryptocurrencies)

        if (localCryptocurrencies.isEmpty()) {
            cryptocurency_progress_bar.visibility = View.GONE
            empty_cryptocurrencies.visibility = View.VISIBLE
            enableAddCryptocurrencyButton(true)
            enableInvestmentButton(false)
            investment_progress_bar.visibility = View.GONE
            empty_investments.visibility = View.VISIBLE
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

    private fun handleCryptocurrencyFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                cryptocurency_progress_bar.visibility = View.GONE
                cryptocurrencies_error.visibility = View.VISIBLE
            }
            is Fail.LocalFail -> {
                cryptocurency_progress_bar.visibility = View.GONE
                cryptocurrencies_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handleCryptocurrencyCardViewDtoListSuccess(list: List<CryptocurrencyCardViewDto>) {
        if (list.isEmpty()) {
            cryptocurency_progress_bar.visibility = View.GONE
            empty_cryptocurrencies.visibility = View.VISIBLE
        } else {
            cryptocurrencyAdapter.submitList(list)
            cryptocurency_progress_bar.visibility = View.GONE
            empty_cryptocurrencies.visibility = View.GONE
        }
        enableAddCryptocurrencyButton(true)
    }

    private fun handleInvestmentsSuccess(investmentList: List<Investment>) {
        if (investmentList.isEmpty()) {
            investment_progress_bar.visibility = View.GONE
            empty_investments.visibility = View.VISIBLE
        } else {
            investments = investmentList
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
            investmentAdapter.submitList(investmentCryptocurrency)
            investment_progress_bar.visibility = View.GONE
            empty_investments.visibility = View.GONE
        }
        enableInvestmentButton(true)
    }

    private fun handleInvestmentsFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                investment_progress_bar.visibility = View.GONE
                investment_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handleIsInvestmentDeletedSuccess(isDeleted: Boolean) {
        if (!isDeleted) {
            Toast.makeText(
                this,
                resources.getString(R.string.delete_investment_error),
                Toast.LENGTH_SHORT
            ).show()
        }
        viewModel.getInvestments(localCryptocurrencies.map { it.id })

    }

    private fun handleIsInvestmentUpdatedSuccess(isUpdated: Boolean) {
        if (!isUpdated) {
            Toast.makeText(
                this,
                resources.getString(R.string.edit_investment_error),
                Toast.LENGTH_SHORT
            ).show()
        }
        viewModel.getInvestments(localCryptocurrencies.map { it.id })
    }

    private fun handleIsLocalCryptocurrencyDeletedSuccess(isDeleted: Boolean) {
        if(!isDeleted) {
            Toast.makeText(this, resources.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
            cryptocurency_progress_bar.visibility = View.GONE
        } else {
            viewModel.getLocalCryptocurrencies(user.id)
        }
    }

    private fun show(layout_id: View, show: Boolean) {
        layout_id.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Dialog's handler methods
     */

    private fun showCryptocurrencyDialog() {
        if (!addCryptocurrencyDialog.isAdded) {
            addCryptocurrencyDialog.show(supportFragmentManager, "AddCryptocurrencyDialogFragment")
        }
    }

    override fun onAddCryptocurrencyDialogConfirmClick(dialog: DialogFragment) {
        // Close dialog
        addCryptocurrencyDialog.dismiss()
        // Update cryptocurrencies
        cryptocurency_progress_bar.visibility = View.VISIBLE
        viewModel.getLocalCryptocurrencies(user.id)
    }

    override fun onAddCryptocurrencyDialogCancelClick(dialog: DialogFragment) {
        addCryptocurrencyDialog.dismiss()
    }

    private fun showInvestmentDialog() {
        if (!addInvestmentDialog.isAdded) {
            addInvestmentDialog.show(supportFragmentManager, "AddInvestmentDialogFragment")
        }

    }

    override fun onAddInvestmentDialogConfirmClick(dialog: DialogFragment) {
        // Close dialog
        addInvestmentDialog.dismiss()
        // Update investment
        viewModel.getInvestments(localCryptocurrencies.map { it.id })
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    override fun onAddInvestmentDialogCancelClick(dialog: DialogFragment) {
        addInvestmentDialog.dismiss()
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    private fun showMenuDialog() {
        if (!menuOptionsDialog.isAdded) {
            menuOptionsDialog.show(supportFragmentManager, "MenuOptionsDialog")
        }
    }

    override fun onDeleteInvestmentClickListener(localCryptocurrencyId: Long) {
        investment_progress_bar.visibility = View.VISIBLE
        val investment = investments.first { it.cryptocurrencyId == localCryptocurrencyId }
        viewModel.deleteInvestment(investment.id)
    }

    override fun onEditInvestmentClickListener(localCryptocurrencyId: Long) {
        val cryptocurrencyName = localCryptocurrencies.first { it.id == localCryptocurrencyId }.name
        val totalPurchased =
            investments.first { it.cryptocurrencyId == localCryptocurrencyId }.totalInvested
        editInvestmentDialog = EditInvestmentDialogFragment.newInstance(
            localCryptocurrencyId,
            cryptocurrencyName,
            totalPurchased
        )
        editInvestmentDialog.show(supportFragmentManager, "EditInvestmentDialogFragment")
    }

    override fun onCancelClick() {
        editInvestmentDialog.dismiss()
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    override fun onConfirmEditClick(localCryptocurrencyId: Long, purchasedValue: Double) {
        val actualInvestment = investments.first { it.cryptocurrencyId == localCryptocurrencyId }
        val investmentEdited = Investment(
            actualInvestment.id,
            purchasedValue,
            localCryptocurrencyId
        )
        investment_progress_bar.visibility = View.VISIBLE
        editInvestmentDialog.dismiss()
        viewModel.updateInvestment(investmentEdited)
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    override fun onDeleteCryptocurrencyClickListener(localCryptocurrencyId: Long) {
        cryptocurency_progress_bar.visibility = View.VISIBLE
        viewModel.deleteLocalCryptocurrency(localCryptocurrencyId)
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

    override fun onRefresCryptocurrencyClickListener(localCryptocurrencyId: Long) {
        cryptocurency_progress_bar.visibility = View.VISIBLE
        viewModel.getLocalCryptocurrencies(user.id)
        cryptocurrencyAdapter.notifyDataSetChanged()
    }

}