package com.jsp.financialcalculator.utils

import android.widget.EditText
import java.text.NumberFormat

fun EditText.clearError() {
    error = null
}

fun Double.round(decimals: Int = 2): Double {
    val numFormat = NumberFormat.getInstance()
    return numFormat.parse("%.${decimals}f".format(this)).toDouble()
}