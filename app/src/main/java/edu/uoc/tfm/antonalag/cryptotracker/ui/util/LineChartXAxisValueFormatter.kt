package edu.uoc.tfm.antonalag.cryptotracker.core.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateXAxisValueFormatter: ValueFormatter(){
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return SimpleDateFormat("dd/MM/YYYY", Locale.getDefault()).format(Date(value.toLong()))
    }
}