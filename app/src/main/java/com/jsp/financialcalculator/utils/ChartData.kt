package com.jsp.financialcalculator.utils

import com.jsp.financialcalculator.view.round

object ChartData {
    fun tabulateFutureValue(pastValue : Double, interestRate : Double, term : Int, inflation : Double) : Map<Int, Double>{
        val tabulatedFun = mutableMapOf<Int, Double>()
        for(i in 1..term) {
            tabulatedFun[i] = WaysOfDecision.findFutureValue(
                pastValue = pastValue,
                interestRate = interestRate,
                term = i,
                inflation = inflation)
        }

        tabulatedFun.forEach { (index, value) ->
            logI("Day $index : FV = ${value.round(5)}")
        }

        return tabulatedFun
    }
}