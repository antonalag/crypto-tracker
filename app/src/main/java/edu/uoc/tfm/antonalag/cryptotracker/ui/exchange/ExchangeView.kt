package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.afterTextChangedDelayed
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.roundToString
import edu.uoc.tfm.antonalag.cryptotracker.core.util.NumberUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.GlideApp
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.MenuFragment
import kotlinx.android.synthetic.main.activity_exchange_view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Class responsible for displaying conversion between fiats and cryptocurrencies
 */
class ExchangeView : BaseActivity(), ExchangeFiatDialogFragment.ExchangeFiatDialogListener,
    ExchangeCryptocurrencyDialogFragment.ExchangeCryptocurrencyDialogListener {

    private val TAG = "ExchangeView"

    private val viewModel by viewModel<ExchangeViewModel>()
    private val menuOptionsDialog: MenuFragment = MenuFragment()
    private lateinit var exchangeAdapter: ExchangeAdapter
    private lateinit var cryptocurrencyDialog: ExchangeCryptocurrencyDialogFragment
    private lateinit var fiatDialog: ExchangeFiatDialogFragment
    private lateinit var fiats: List<Fiat>
    private lateinit var localCryptocurrencies: List<LocalCryptocurrency>
    private val user: User = CryptoTrackerApp.instance.user
    private val userPreferences: UserPreferences = CryptoTrackerApp.instance.userPreferences
    private lateinit var fiatSelected: Fiat
    private lateinit var cryptocurrencySelected: CryptocurrencyListViewDto
    private lateinit var localCryptocurrencySelected: LocalCryptocurrency


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.main_toolbar, R.id.main_toolbar)
        initSetLayout(R.layout.activity_exchange_view)
        initUI()
        initViewModelObservers()
        initRequests()
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
        // Set click listeners
        exchange_trash_button.setOnClickListener {
            resetUI()
        }
        exchange_fiat_container.setOnClickListener {
            showFiatDialog()
        }
        exchange_cryptocurrency_container.setOnClickListener {
            showCryptocurrencyDialog()
        }

        // Initialize recycler view
        exchange_list_recycler_view.layoutManager = LinearLayoutManager(this)
        exchangeAdapter = ExchangeAdapter()
        exchange_list_recycler_view.adapter = exchangeAdapter

        // Set text changed value listener in EditText
        left_side_value_exchange.afterTextChangedDelayed {
            // Hide keyboard
            val view = this@ExchangeView.currentFocus
            view?.let { vw ->
                val imm: InputMethodManager =
                    this@ExchangeView.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(vw.windowToken, 0)
            }
            // initialize conversion
            initExchange(it)
        }
    }

    /**
     * Configure the ExchangeViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(localCryptocurrencies, ::handleLocalCryptocurrenciesSuccess)
            fail(fail, ::handleExchangeViewFail)
        }

        with(viewModel) {
            observe(fiats, ::handleFiatsSuccess)
            fail(fail, ::handleExchangeViewFail)
        }
    }

    /**
     * Initializes the necessary requests
     */
    private fun initRequests() {
        // Get fiats
        viewModel.getFiats()
    }

    /**
     * It's called when the request for fiats information is successful
     */
    private fun handleFiatsSuccess(list: List<Fiat>) {
        Log.v(TAG, resources.getString(R.string.exchange_view_fiats_request_successful))
        fiats = list
        fiatSelected =
            list.first { it.name == userPreferences.fiat && it.symbol == userPreferences.fiatSymbol }
        // Set fiat data on screen
        setFiatInfo()

        // set fiatDialog
        if (::fiatDialog.isInitialized && fiatDialog.isAdded) {
            fiatDialog.dismiss()
        }
        fiatDialog = ExchangeFiatDialogFragment.newInstance(Gson().toJson(fiats))

        // Get local cryptocurrencies
        viewModel.getLocalCryptocurrencies(user.id)
    }

    /**
     * It's called when the request for local cryptocurrencies is successful
     */
    private fun handleLocalCryptocurrenciesSuccess(list: List<LocalCryptocurrency>) {
        Log.v(TAG, resources.getString(R.string.local_cryptocurrencies_request_succesful))
        localCryptocurrencies = list
        // Init cryptocurrency dialog
        initCryptocurrencyDialog(userPreferences.fiat)
        showView(exchange_progress_bar, false)
        // Check if local cryptocurrencies not exist
        if (localCryptocurrencies.isEmpty()) {
            Log.v(TAG, resources.getString(R.string.local_cryptocurrencies_request_not_found))
            // Show message to the user
            Toast.makeText(
                this,
                resources.getString(R.string.exchange_view_empty_local_cryptocurrency_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Enabled quantity editText
            left_side_value_exchange.isEnabled = true
            showView(exchange_progress_bar, false)
            // Set first local cryptocurrency in converter
            val localCryptocurrency = localCryptocurrencies.first()
            localCryptocurrencySelected = localCryptocurrency
            // Set cryptocurrency data on screen
            setCryptocurrencyInfo(localCryptocurrency)
        }
    }

    /**
     * Initializes cryptocurency selection dialog
     */
    private fun initCryptocurrencyDialog(fiatName: String) {
        // Set cryptocurrencyDialog
        if (::cryptocurrencyDialog.isInitialized && cryptocurrencyDialog.isAdded) {
            cryptocurrencyDialog.dismiss()
        }
        cryptocurrencyDialog = ExchangeCryptocurrencyDialogFragment.newInstance(fiatName)
    }

    /**
     * It's called when the requests have failed
     */
    private fun handleExchangeViewFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.local_cryptocurrencies_request_failed))
        when (fail) {
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showView(exchange_progress_bar, false)
                showView(exchange_error, true)
            }
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                showView(exchange_progress_bar, false)
                showView(exchange_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(exchange_progress_bar, false)
                showView(exchange_error, true)
            }
        }
    }

    /**
     * Set fiat data on screen
     */
    private fun setFiatInfo() {
        // Set fiat image
        GlideApp.with(this)
            .load(fiatSelected.imageUrl)
            .centerCrop()
            .into(left_side_image_exchange)
        // Set symbol
        left_side_symbol_exchange.text = fiatSelected.name
    }

    /**
     * Set cryptocurrency data on screen. If the user has not selected any cryptocurrency,
     * the first cryptocurrency stored locally is automatically selected.
     */
    private fun setCryptocurrencyInfo(localCryptocurrency: LocalCryptocurrency?) {
        val icon = localCryptocurrency?.icon ?: cryptocurrencySelected.icon
        val symbol = localCryptocurrency?.symbol ?: cryptocurrencySelected.symbol
        // Set cryptocurrency image
        GlideApp.with(this)
            .load(icon)
            .centerCrop()
            .into(right_side_image_exchange)
        // Set symbol
        right_side_symbol_exchange.text = symbol
    }

    /**
     * Reset all information set on the screen
     */
    private fun resetUI() {
        // Reset fiat symbol
        left_side_value_exchange.setText("")
        // Reset cryptocurrency symbol
        right_side_exchange_value.text = ""
        // Hide calculated conversion
        showView(calculated_conversion_container, false)
        // Hide recycler view
        showView(exchange_list_recycler_view_container, false)
    }

    /**
     * Initializes the conversion between a fiat and cryptocurrencies
     */
    private fun initExchange(quantity: String) {
        // Check if the user has entered and amount to convert, because if no amount
        // has been entered the conversion does not start.
        if (!quantity.isNullOrEmpty()) {
            // Get quantity to convert
            val quantityValue = quantity.toDouble()
            // Set quantity
            calculated_conversion_value.text = quantity + " ${fiatSelected.symbol} = "
            // Show calculated version
            showView(calculated_conversion_container, true)
            // Calculate selected value
            calculateCryptocurrencySelectedValue(quantityValue)
            // Show recycler view
            showView(exchange_list_recycler_view_container, true)
            // Set data in exhange adapter
            exchangeAdapter.setData(
                fiatSelected,
                fiats.first { it.name == userPreferences.fiat && it.symbol == userPreferences.fiatSymbol },
                quantityValue
            )
            // If the user selected a cryptocurrency that is stored locally, not shown in the recycler view
            val name =
                if (::cryptocurrencySelected.isInitialized) cryptocurrencySelected.name.toLowerCase() else localCryptocurrencySelected.name.toLowerCase()
            exchangeAdapter.submitList(
                localCryptocurrencies.filterNot { it.name == name }
            )
            exchangeAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Calculates the selected cryptocurrency value in the fiat selected.
     * If the user has not selected any cryptocurrency, the first cryptocurrency
     * stored locally is automatically calculated.
     */
    private fun calculateCryptocurrencySelectedValue(quantity: Double) {
        if (::cryptocurrencySelected.isInitialized) {
            val cryptocurrencyValue =
                NumberUtil.ruleOfThreeCalculateYValue(cryptocurrencySelected.price, 1, quantity)
            right_side_exchange_value.text = cryptocurrencyValue.roundToString(4)
        } else {
            val fiatUserPreferences =
                fiats.first { it.name == userPreferences.fiat && it.symbol == userPreferences.fiatSymbol }
            val priceInDolars = NumberUtil.ruleOfThreeCalculateYValue(
                fiatUserPreferences.rate,
                1,
                localCryptocurrencySelected.price
            )
            val priceInFiatSelected =
                NumberUtil.ruleOfThreeCalculateXValue(fiatSelected.rate, 1, priceInDolars)
            val cryptocurrencyValue =
                NumberUtil.ruleOfThreeCalculateYValue(priceInFiatSelected, 1, quantity)
            right_side_exchange_value.text = cryptocurrencyValue.roundToString(4)
        }
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
     * Show fiat dialog
     */
    private fun showFiatDialog() {
        if (!fiatDialog.isAdded) {
            fiatDialog.show(supportFragmentManager, "ExchangeFiatDialog")
        }
    }

    /**
     * Show Cryptocurrency dialog
     */
    private fun showCryptocurrencyDialog() {
        if (!cryptocurrencyDialog.isAdded) {
            cryptocurrencyDialog.show(supportFragmentManager, "ExchangeCryptocurrencyDialog")
        }
    }

    /**
     * Callback function that detect when the user clicks on
     * fiat dialog cancel button.
     */
    override fun onExchangeFiatDialogCancelClick() {
        // Hide fiat dialog
        fiatDialog.dismiss()
    }

    /**
     * Callback function that detect when the user clicks on
     * fiat dialog confirm button.
     */
    override fun onExchangeFiatDialogConfirmClick(fiat: Fiat) {
        // Hide fiat dialog
        fiatDialog.dismiss()
        // Change fiat selected
        fiatSelected = fiat
        // Set fiat data on screen
        setFiatInfo()
        // Initializes exchange
        initExchange(left_side_value_exchange.text.toString())

    }

    /**
     * Callback function that detect when the user clicks on
     * cryptocurrency dialog cancel button.
     */
    override fun onExchangeCryptocurrencyDialogCancelClick() {
        // hide cryptocurrency dialog
        cryptocurrencyDialog.dismiss()
    }

    /**
     * Callback function that detect when the user clicks on
     * cryptocurrency dialog confirm button.
     */
    override fun onExchangeCryptocurrencyDialogConfirmClick(cryptocurrencyListViewDto: CryptocurrencyListViewDto) {
        // Hide cryptocurrency dialog
        cryptocurrencyDialog.dismiss()
        // Change cryptocurrency selected
        cryptocurrencySelected = cryptocurrencyListViewDto
        // Set cryptocurrency data on screen
        setCryptocurrencyInfo(null)
        // Enabled quantity editText
        left_side_value_exchange.isEnabled = true
        // Initialize exchange if the user has entered a quantity
        if (!left_side_value_exchange.text.toString().isNullOrEmpty()) {
            initExchange(left_side_value_exchange.text.toString())
        }
    }

}