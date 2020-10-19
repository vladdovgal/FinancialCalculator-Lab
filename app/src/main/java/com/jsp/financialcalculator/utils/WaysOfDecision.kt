package com.jsp.financialcalculator.utils

import kotlin.math.log
import kotlin.math.pow

object WaysOfDecision {
    private const val YEAR_BASE = 365
    fun findFutureValue(pastValue : Double, term : Int, interestRate : Double) : Double {
        val (years , fraction) = simplifyTerm(term)
        val interestRateDecimal = interestRate * 0.01
        return pastValue * (1 + interestRateDecimal).pow(years) * (1 +interestRateDecimal*fraction)
    }

    fun findPastValue(futureValue : Double, term : Int, interestRate : Double): Double {
        val (years , fraction) = simplifyTerm(term)
        val interestRateDecimal = interestRate * 0.01
        return futureValue / ((1 + interestRateDecimal).pow(years) * (1 +interestRateDecimal*fraction))
    }

    fun findTerm(pastValue: Double, futureValue: Double, interestRate: Double): Pair<Int, Int> {
        val logBase = 1 + interestRate * 0.01
        val decimalResult = log(futureValue/ pastValue, logBase)
        val years = decimalResult.toInt()
        val days = ((decimalResult - years) * YEAR_BASE).toInt()
        return Pair(years, days)
    }

    private fun simplifyTerm(term : Int) = Pair(
        term / YEAR_BASE,
        (term % YEAR_BASE).toDouble() / YEAR_BASE
    )
}