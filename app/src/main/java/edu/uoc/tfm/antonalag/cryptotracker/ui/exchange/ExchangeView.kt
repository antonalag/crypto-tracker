package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
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
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.Cryptocurrency
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

        exchange_list_recycler_view.layoutManager = LinearLayoutManager(this)
        exchangeAdapter = ExchangeAdapter()
        exchange_list_recycler_view.adapter = exchangeAdapter

        // Set text change listener
        left_side_value_exchange.afterTextChangedDelayed {
            val view = this@ExchangeView.currentFocus
            view?.let { vw ->
                val imm: InputMethodManager =
                    this@ExchangeView.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(vw.windowToken, 0)
            }
            initExchange(it)
        }
    }

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

    private fun initRequests() {
        viewModel.getFiats()
    }

    private fun handleFiatsSuccess(list: List<Fiat>) {
        fiats = list
        fiatSelected =
            list.first { it.name == userPreferences.fiat && it.symbol == userPreferences.fiatSymbol }
        setFiatInfo()

        // set fiatDialog
        if (::fiatDialog.isInitialized && fiatDialog.isAdded) {
            fiatDialog.dismiss()
        }
        fiatDialog = ExchangeFiatDialogFragment.newInstance(Gson().toJson(fiats))

        viewModel.getLocalCryptocurrencies(user.id)
    }

    private fun handleLocalCryptocurrenciesSuccess(list: List<LocalCryptocurrency>) {
        localCryptocurrencies = list
        initCryptocurrencyDialog(userPreferences.fiat)
        showLoading(false)

        if (localCryptocurrencies.isEmpty()) {
            Toast.makeText(
                this,
                "No sigues a ninguna criptomoneda. Por favor, selecciona Pulsa el botón de criptomonedas para convertir un valor",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Enabled quantity editText
            left_side_value_exchange.isEnabled = true
            showLoading(false)
            val localCryptocurrency = localCryptocurrencies.first()
            localCryptocurrencySelected = localCryptocurrency
            setCryptocurrencyInfo(localCryptocurrency)
        }
    }

    private fun initCryptocurrencyDialog(fiatName: String) {
        // Set cryptocurrencyDialog
        if (::cryptocurrencyDialog.isInitialized && cryptocurrencyDialog.isAdded) {
            cryptocurrencyDialog.dismiss()
        }
        cryptocurrencyDialog = ExchangeCryptocurrencyDialogFragment.newInstance(fiatName)
    }

    private fun handleExchangeViewFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showLoading(false)
                showError(true)
            }
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
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

    private fun setFiatInfo() {
        // Set fiat image
        GlideApp.with(this)
            .load(fiatSelected.imageUrl)
            .centerCrop()
            .into(left_side_image_exchange)
        // Set symbol
        left_side_symbol_exchange.text = fiatSelected.name
    }

    private fun setCryptocurrencyInfo(localCryptocurrency: LocalCryptocurrency?) {
        if (localCryptocurrency != null) {
            GlideApp.with(this)
                .load(localCryptocurrency.icon)
                .centerCrop()
                .into(right_side_image_exchange)
            right_side_symbol_exchange.text = localCryptocurrency.symbol
        } else {
            GlideApp.with(this)
                .load(cryptocurrencySelected.icon)
                .centerCrop()
                .into(right_side_image_exchange)
            right_side_symbol_exchange.text = cryptocurrencySelected.symbol
        }
    }

    private fun resetUI() {
        left_side_value_exchange.setText("")
        right_side_exchange_value.text = ""
        calculated_conversion_container.visibility = View.INVISIBLE
        exchange_list_recycler_view_container.visibility = View.INVISIBLE
    }

    private fun initExchange(quantity: String) {
        if (!quantity.isNullOrEmpty()) {
            val quantityValue = quantity.toDouble()
            calculated_conversion_value.text = quantity + " ${fiatSelected.symbol} = "
            calculated_conversion_container.visibility = View.VISIBLE
            calculateCryptocurrencySelectedValue(quantityValue)
            exchange_list_recycler_view_container.visibility = View.VISIBLE
            //exchangeAdapter = ExchangeAdapter()
            //exchange_list_recycler_view.adapter = exchangeAdapter
            exchangeAdapter.setData(
                fiatSelected,
                fiats.first { it.name == userPreferences.fiat && it.symbol == userPreferences.fiatSymbol },
                quantityValue
            )
            val name =
                if (::cryptocurrencySelected.isInitialized) cryptocurrencySelected.name.toLowerCase() else localCryptocurrencySelected.name.toLowerCase()
            exchangeAdapter.submitList(
                localCryptocurrencies.filterNot { it.name == name }
            )
            exchangeAdapter.notifyDataSetChanged()
        }
    }

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

    private fun showMenuDialog() {
        if (!menuOptionsDialog.isAdded) {
            menuOptionsDialog.show(supportFragmentManager, "MenuOptionsDialog")
        }
    }

    private fun showFiatDialog() {
        if (!fiatDialog.isAdded) {
            fiatDialog.show(supportFragmentManager, "ExchangeFiatDialog")
        }
    }

    private fun showCryptocurrencyDialog() {
        if (!cryptocurrencyDialog.isAdded) {
            cryptocurrencyDialog.show(supportFragmentManager, "ExchangeCryptocurrencyDialog")
        }
    }

    private fun showLoading(show: Boolean) {
        exchange_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(show: Boolean) {
        exchange_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onExchangeFiatDialogCancelClick() {
        fiatDialog.dismiss()
    }

    override fun onExchangeFiatDialogConfirmClick(fiat: Fiat) {
        fiatDialog.dismiss()
        fiatSelected = fiat
        setFiatInfo()
        initExchange(left_side_value_exchange.text.toString())

    }

    override fun onExchangeCryptocurrencyDialogCancelClick() {
        cryptocurrencyDialog.dismiss()
    }

    override fun onExchangeCryptocurrencyDialogConfirmClick(cryptocurrencyListViewDto: CryptocurrencyListViewDto) {
        cryptocurrencyDialog.dismiss()
        cryptocurrencySelected = cryptocurrencyListViewDto
        setCryptocurrencyInfo(null)
        // Enabled quantity editText
        left_side_value_exchange.isEnabled = true
        if (!left_side_value_exchange.text.toString().isNullOrEmpty()) {
            initExchange(left_side_value_exchange.text.toString())
        }

    }

}