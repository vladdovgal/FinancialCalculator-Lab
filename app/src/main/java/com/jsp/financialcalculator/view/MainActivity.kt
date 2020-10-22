package com.jsp.financialcalculator.view

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jsp.financialcalculator.R
import com.jsp.financialcalculator.math_logic.WaysOfDecision
import com.jsp.financialcalculator.utils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var pastValue : Double = 0.0
    private var futureValue : Double = 0.0
    private var interestRate : Double = 0.0
    private var term : Int = 0
    private var unknownVariable : Parameter? = null
    private var isRateNominal = false
    private var result : String = ""
    private var chartUtils = ChartUtils(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeData()
    }

    private fun observeData() {
        setUnknownValueSwitcher()
        observeIfInflation()

        btnCalculate.setOnClickListener {
            findSolution()
        }

        switchRate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isRateNominal = true
                etTimesPerYear.visibility = View.VISIBLE
                tvTimes.visibility = View.VISIBLE
                interestRateHint.text = resources.getString(R.string.nominal_rate)
            } else {
                isRateNominal = false
                etTimesPerYear.visibility = View.INVISIBLE
                tvTimes.visibility = View.INVISIBLE
                interestRateHint.text = resources.getString(R.string.interest_rate)
                etTimesPerYear.setText("")
            }
        }
    }

    private fun findSolution() {
        if (validateInput()) return

        // if taking into account inflation
        val inflation = if (etInflation.text.isNotEmpty() && cbIfInflation.isChecked) {
            etInflation.text.toString().toDouble()
        } else 0.0
        // if using nominal rate
        val chargesPerYear: Int = if(
            etTimesPerYear.text.isNotEmpty() && etTimesPerYear.text.toString().toInt() != 0) {
            Integer.parseInt(etTimesPerYear.text.toString())
        } else 1

        when(unknownVariable) {
            Parameter.FV -> {
                result = resources.getString(
                    R.string.fv_equals,
                    WaysOfDecision.findFutureValue(
                        pastValue,
                        term,
                        interestRate,
                        inflation,  
                        chargesPerYear
                    )
                        .round(4)
                        .toString()
                )
                // build chart using
                val data = chartUtils.tabulateFutureValue(pastValue, interestRate, term, inflation, chargesPerYear)
                chartUtils.buildLinearChart(data, this)
                chart.invalidate() // refresh data in chart
                chart.visibility = View.VISIBLE
            }
            Parameter.PV -> {
                result = resources.getString(
                    R.string.pv_equals,
                    WaysOfDecision.findPastValue(
                        futureValue,
                        term,
                        interestRate,
                        inflation,
                        chargesPerYear)
                        .round(4)
                        .toString()
                )
                chart.visibility = View.GONE
            }
            Parameter.n -> {
                result = resources.getString(
                    R.string.n_equals,
                    WaysOfDecision.findTerm(
                        pastValue,
                        futureValue,
                        interestRate,
                        inflation,
                        chargesPerYear
                    ).first.toString(),
                    WaysOfDecision.findTerm(
                        pastValue,
                        futureValue,
                        interestRate,
                        inflation,
                        chargesPerYear
                    ).second.toString()
                )
                chart.visibility = View.GONE
            }
            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show()
        }
        tvMainResult.text = result
        tvDiscountRate.text = resources.getString(
            R.string.discount_rate,
            WaysOfDecision.calculateEquivalentDiscountRate(interestRate).round(2).toString()
        )
        tvMainResult.visibility = View.VISIBLE
    }

    private fun validateInput(): Boolean {
        if (unknownVariable == null) {
            Toast.makeText(this, "Виберіть невідомий параметр", Toast.LENGTH_LONG).show()
            return true
        }
        if (etFutureValue.text.isNotEmpty()) {
            futureValue = etFutureValue.text.toString().toDouble()
        } else if (unknownVariable != Parameter.FV) {
            etFutureValue.error = resources.getString(R.string.empty_field)
        }

        if (etPastValue.text.isNotEmpty() ) {
            pastValue = etPastValue.text.toString().toDouble()
        } else if (unknownVariable != Parameter.PV) {
            etPastValue.error = resources.getString(R.string.empty_field)
        }

        if (etPastValue.text.isNotEmpty()  && etFutureValue.text.isNotEmpty()) {
            if (etPastValue.text.toString().toDouble() > etFutureValue.text.toString().toDouble()) {
                Toast.makeText(this, resources.getString(R.string.pv_more_fv), Toast.LENGTH_LONG).show()
            }
        }

        if (etTerm.text.isNotEmpty()) {
            term = etTerm.text.toString().toInt()
        } else if (unknownVariable != Parameter.n) {
            etTerm.error = resources.getString(R.string.empty_field)
        }

        if (etInterestRate.text.isNotEmpty()) {
            interestRate = etInterestRate.text.toString().toDouble()
        } else {
            etInterestRate.error = resources.getString(R.string.empty_field)
        }
        return false
    }

    private fun observeIfInflation() {
        cbIfInflation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                llInflationParams.visibility = View.VISIBLE
            } else {
                llInflationParams.visibility = View.INVISIBLE
            }
        }
    }

    private fun setUnknownValueSwitcher() {
        rbFutureValue.setOnClickListener(radioButtonClickListener)
        rbPastValue.setOnClickListener(radioButtonClickListener)
        rbTerm.setOnClickListener(radioButtonClickListener)
    }

    private val radioButtonClickListener =
        View.OnClickListener { v ->
            when (v as RadioButton) {
                rbFutureValue -> {
                    etFutureValue.isEnabled = false
                    etPastValue.isEnabled = true
                    etTerm.isEnabled = true
                    etFutureValue.text.clear()
                    etFutureValue.clearError()
                    unknownVariable = Parameter.FV
                }
                rbPastValue -> {
                    etFutureValue.isEnabled = true
                    etPastValue.isEnabled = false
                    etTerm.isEnabled = true
                    etPastValue.text.clear()
                    etPastValue.clearError()
                    unknownVariable = Parameter.PV
                }
                rbTerm -> {
                    etFutureValue.isEnabled = true
                    etPastValue.isEnabled = true
                    etTerm.isEnabled = false
                    etTerm.text.clear()
                    etTerm.clearError()
                    unknownVariable = Parameter.n
                }
            }
        }
}
