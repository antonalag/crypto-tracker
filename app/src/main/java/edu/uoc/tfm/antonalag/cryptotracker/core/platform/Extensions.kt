package edu.uoc.tfm.antonalag.cryptotracker.core.platform

import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.round


/**
 * Extension that round a decimal number to specific N decimal places
 */
fun Double.roundToString(decimals: Int): String {
    return String.format("%.${decimals}f", this)
}

/**
 * Extension that execute a trigger function after delayed teime
 */
fun EditText.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        var timer: CountDownTimer? = null

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            timer?.cancel()
            timer = object : CountDownTimer(1000, 1500) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    afterTextChanged.invoke(s.toString())
                }

            }.start()
        }

    })
}