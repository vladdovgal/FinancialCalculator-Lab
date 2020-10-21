package com.jsp.financialcalculator.utils

import kotlin.math.log
import kotlin.math.pow

object WaysOfDecision {
    private const val YEAR_BASE = 365
    // todo implement nominal rate
    fun findFutureValue(pastValue : Double, term : Int, interestRate : Double, inflation : Double, chargesPerYear : Int) : Double {
        val (years , fraction) = simplifyTerm(term)
        val (interestRateDecimal, inflationRateDecimal) = Pair(interestRate * 0.01, inflation* 0.01)
        val preciseRate = interestRateDecimal + inflationRateDecimal + interestRateDecimal * inflationRateDecimal
        return pastValue * (1 + preciseRate/chargesPerYear).pow(years * chargesPerYear) * (1 +preciseRate*fraction)
    }

    fun findPastValue(futureValue : Double, term : Int, interestRate : Double, inflation: Double, chargesPerYear : Int): Double {
        val (years , fraction) = simplifyTerm(term)
        val (interestRateDecimal, inflationRateDecimal) = Pair(interestRate * 0.01, inflation* 0.01)
        val preciseRate = interestRateDecimal + inflationRateDecimal + interestRateDecimal * inflationRateDecimal
        return futureValue / ((1 + preciseRate/chargesPerYear).pow(years * chargesPerYear) * (1 +preciseRate*fraction))
    }

    fun findTerm(pastValue: Double, futureValue: Double, interestRate: Double, inflation: Double,  chargesPerYear : Int): Pair<Int, Int> {
        val (interestRateDecimal, inflationRateDecimal) = Pair(interestRate * 0.01, inflation* 0.01)
        val preciseRate = interestRateDecimal + inflationRateDecimal + interestRateDecimal * inflationRateDecimal
        val logBase = 1 + preciseRate / chargesPerYear
        val decimalResult = log(futureValue / pastValue, logBase) / chargesPerYear
        val years = decimalResult.toInt()
        val days = ((decimalResult - years) * YEAR_BASE).toInt()
        return Pair(years, days)
    }

    private fun simplifyTerm(term : Int) = Pair(
        term / YEAR_BASE,
        (term % YEAR_BASE).toDouble() / YEAR_BASE
    )

    fun calculateEquivalentDiscountRate(interestRate: Double): Double = interestRate / (interestRate + 1)
}