package edu.uoc.tfm.antonalag.cryptotracker.ui.userpreferences

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import kotlinx.android.synthetic.main.activity_home_view.*
import kotlinx.android.synthetic.main.activity_user_preferences_view.*
import kotlinx.android.synthetic.main.detail_toolbar_edit_button.*
import kotlinx.android.synthetic.main.detail_toolbar_edit_button.menu_back_button
import kotlinx.android.synthetic.main.detail_toolbar_refresh_button.*
import org.koin.android.viewmodel.ext.android.viewModel

class UserPreferencesView : BaseActivity() {

    private val TAG = "UserPreferencesView"

    private val viewModel by viewModel<UserPreferencesViewModel>()
    private val fiatCurrencyAdapter = FiatCurrencyAdapter()

    private lateinit var userPreferences: UserPreferences
    private lateinit var userPreferencesToSave: UserPreferences
    private lateinit var timeIntervalSelected: String
    private lateinit var dataUpdateSelected: String
    private var isEditButtonClicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.detail_toolbar_edit_button, R.id.detail_edit_button_toolbar)
        initSetLayout(R.layout.activity_user_preferences_view)
        initUI()
        initViewModelObservers()
        initRequests()
    }

    private fun initUI() {
        // Set user preferences
        userPreferences = CryptoTrackerApp.instance.userPreferences
        // Set back button
        menu_back_button.setOnClickListener {
            super.onBackPressed()
        }
        // Set initial data
        setTimeInterval()
        setDataUpdate()
        // Set on click listeners
        menu_edit_button.setOnClickListener {
            if (!isEditButtonClicked) {
                isEditButtonClicked = true
            }
            enableAll(isEditButtonClicked)
        }
        confirm_user_preferences.setOnClickListener {
            if (validate()) {
                savePreferences()
            } else {
                Toast.makeText(this, "Debe seleccionar una moneda, un intervalo de tiempo y un tiempo de actualización de datos", Toast.LENGTH_SHORT).show()
            }
        }
        cancel_user_preferences.setOnClickListener {
            isEditButtonClicked = false
            setTimeInterval()
            setDataUpdate()
            enableAll(isEditButtonClicked)
        }
        // init recyclerview
        fiatCurrencyAdapter.fiatSelected = userPreferences.fiat
        fiat_currencies_list_recycler_view.layoutManager = LinearLayoutManager(this)
        fiat_currencies_list_recycler_view.adapter = fiatCurrencyAdapter
    }

    private fun initViewModelObservers() {
        with(viewModel) {
            observe(fiatCurrencies, ::handleFiatCurrenciesSuccess)
            fail(fail, ::handleUserPreferencesFail)
        }

        with(viewModel) {
            observe(isUserPreferencesUpdated, ::handleIsUserPreferencesUpdateSuccess)
            fail(fail, ::handleUserPreferencesFail)
        }
    }

    private fun initRequests() {
        viewModel.getFiats()
    }

    private fun enableAll(enable: Boolean) {
        radiobutton_ti_one_day.isEnabled = enable
        radiobutton_ti_one_week.isEnabled = enable
        radiobutton_ti_one_month.isEnabled = enable
        radiobutton_ti_three_months.isEnabled = enable
        radiobutton_ti_six_months.isEnabled = enable
        radiobutton_ti_one_year.isEnabled = enable
        radiobutton_du_five_minutes.isEnabled = enable
        radiobutton_du_fifteen_minutes.isEnabled = enable
        radiobutton_du_thirty_minutes.isEnabled = enable
        radiobutton_du_one_hour.isEnabled = enable
        radiobutton_du_three_hours.isEnabled = enable
        radiobutton_du_six_hours.isEnabled = enable
        radiobutton_du_nine_hours.isEnabled = enable
        radiobutton_du_one_day.isClickable = enable
        cancel_user_preferences.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        confirm_user_preferences.visibility = if (enable) View.VISIBLE else View.INVISIBLE
    }

    private fun setTimeInterval() {
        val radioButton = when (userPreferences.timeInterval) {
            "24h" ->
                findViewById(R.id.radiobutton_ti_one_day)
            "1w" ->
                findViewById(R.id.radiobutton_ti_one_week)
            "1m" ->
                findViewById(R.id.radiobutton_ti_one_month)
            "3m" ->
                findViewById(R.id.radiobutton_ti_three_months)
            "6m" ->
                findViewById(R.id.radiobutton_ti_six_months)
            "1y" ->
                findViewById(R.id.radiobutton_ti_one_year)
            else ->
                findViewById<RadioButton>(R.id.radiobutton_ti_one_month)
        }
        radioButton.isChecked = true
        timeIntervalSelected = userPreferences.timeInterval
    }

    private fun setDataUpdate() {
        val radioButton = when (userPreferences.dataUpdate) {
            "5m" ->
                findViewById(R.id.radiobutton_du_five_minutes)
            "15m" ->
                findViewById(R.id.radiobutton_du_fifteen_minutes)
            "30m" ->
                findViewById(R.id.radiobutton_du_thirty_minutes)
            "1h" ->
                findViewById(R.id.radiobutton_du_one_hour)
            "3h" ->
                findViewById(R.id.radiobutton_du_three_hours)
            "6h" ->
                findViewById(R.id.radiobutton_du_six_hours)
            "9h" ->
                findViewById(R.id.radiobutton_du_nine_hours)
            "24h" ->
                findViewById(R.id.radiobutton_du_one_day)
            else ->
                findViewById<RadioButton>(R.id.radiobutton_du_three_hours)
        }
        radioButton.isChecked = true
        dataUpdateSelected = userPreferences.dataUpdate
    }


    fun onTimeIntervalRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked
            val checked = view.isChecked
            // Check which radio radio button was clicked
            when (view.id) {
                R.id.radiobutton_ti_one_day ->
                    if (checked)
                        timeIntervalSelected = "24h"
                R.id.radiobutton_ti_one_week ->
                    if (checked)
                        timeIntervalSelected = "1w"
                R.id.radiobutton_ti_one_month ->
                    if (checked)
                        timeIntervalSelected = "1m"
                R.id.radiobutton_ti_three_months ->
                    if (checked)
                        timeIntervalSelected = "3m"
                R.id.radiobutton_ti_six_months ->
                    if (checked)
                        timeIntervalSelected = "6m"
                R.id.radiobutton_ti_one_year ->
                    if (checked)
                        timeIntervalSelected = "1y"
            }
            clearOtherTimeIntervalRadioButtons(view.parent as View)
        }
    }

    private fun clearOtherTimeIntervalRadioButtons(parentView: View) {
        if (parentView.id == R.id.radiogroup_time_interval_right) {
            radiogroup_time_interval_left.clearCheck()
        } else {
            radiogroup_time_interval_right.clearCheck()
        }
    }

    fun onDataUpdateRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked
            val checked = view.isChecked
            // Check which radio radio button was clicked
            when (view.id) {
                R.id.radiobutton_du_five_minutes ->
                    if (checked)
                        dataUpdateSelected = "5m"
                R.id.radiobutton_du_fifteen_minutes ->
                    if (checked)
                        dataUpdateSelected = "15m"
                R.id.radiobutton_du_thirty_minutes ->
                    if (checked)
                        dataUpdateSelected = "30m"
                R.id.radiobutton_du_one_hour ->
                    if (checked)
                        dataUpdateSelected = "1h"
                R.id.radiobutton_du_three_hours ->
                    if (checked)
                        dataUpdateSelected = "3h"
                R.id.radiobutton_du_six_hours ->
                    if (checked)
                        dataUpdateSelected = "6h"
                R.id.radiobutton_du_nine_hours ->
                    if (checked)
                        dataUpdateSelected = "9h"
                R.id.radiobutton_du_one_day ->
                    if (checked)
                        dataUpdateSelected = "24h"
            }
            clearOtherDataUpdateRadioButtons(view.parent as View)
        }
    }

    private fun clearOtherDataUpdateRadioButtons(parentView: View) {
        if (parentView.id == R.id.radiogroup_data_update_right) {
            radiogroup_data_update_left.clearCheck()
        } else {
            radiogroup_data_update_right.clearCheck()
        }
    }

    private fun handleFiatCurrenciesSuccess(fiatList: List<Fiat>) {
        fiatCurrencyAdapter.submitList(fiatList)
        showLoading(false)
    }

    private fun savePreferences() {
        showLoading(true)
        userPreferencesToSave = UserPreferences(
            userPreferences.id,
            fiatCurrencyAdapter.fiatSelected,
            fiatCurrencyAdapter.fiatSymbolSelected,
            timeIntervalSelected,
            dataUpdateSelected,
            userPreferences.userId
        )
        viewModel.updatePreferences(userPreferencesToSave)
    }

    private fun handleIsUserPreferencesUpdateSuccess(isUpdated: Boolean) {
        if (isUpdated) {
            CryptoTrackerApp.instance.userPreferences = userPreferencesToSave
            isEditButtonClicked = false
            enableAll(isEditButtonClicked)
            setTimeInterval()
            setDataUpdate()
            showLoading(false)
        }
    }

    private fun validate(): Boolean {
        return !fiatCurrencyAdapter.fiatSelected.isNullOrEmpty() && !timeIntervalSelected.isNullOrEmpty() && !dataUpdateSelected.isNullOrEmpty()
    }

    private fun showLoading(show: Boolean) {
        user_preferences_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun handleUserPreferencesFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                showLoading(false)
                investment_error.visibility = View.VISIBLE
            }
            is Fail.LocalFail -> {
                showLoading(false)
                investment_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }
}