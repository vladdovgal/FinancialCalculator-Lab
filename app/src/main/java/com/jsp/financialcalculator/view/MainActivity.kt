package com.jsp.financialcalculator.view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.jsp.financialcalculator.R
import com.jsp.financialcalculator.utils.ChartData
import com.jsp.financialcalculator.utils.Parameter
import com.jsp.financialcalculator.utils.WaysOfDecision
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        var pastValue : Double = 0.0
        var futureValue : Double = 0.0
        var interestRate : Double = 0.0
        var term : Int = 0
        var unknownVariable : Parameter? = null
    }

    private var result : String = ""

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
    }

    private fun findSolution() {
        if (validateInput()) return

        var inflation : Double = 0.0
        if (etInflation.text.isNotEmpty() && cbIfInflation.isChecked) {
            inflation = etInflation.text.toString().toDouble()
        }
        when(unknownVariable) {
            Parameter.FV -> {
                result = resources.getString(
                    R.string.fv_equals,
                    WaysOfDecision.findFutureValue(pastValue, term, interestRate, inflation)
                        .round(4)
                        .toString()
                )
            }
            Parameter.PV -> {
                result = resources.getString(
                    R.string.pv_equals,
                    WaysOfDecision.findPastValue(futureValue, term, interestRate, inflation)
                        .round(4)
                        .toString()
                )
            }
            Parameter.n -> {
                result = resources.getString(
                    R.string.n_equals,
                    WaysOfDecision.findTerm(
                        pastValue,
                        futureValue,
                        interestRate,
                        inflation
                    ).first.toString(),
                    WaysOfDecision.findTerm(
                        pastValue,
                        futureValue,
                        interestRate,
                        inflation
                    ).second.toString()
                )
            }
            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show()
        }
        tvMainResult.text = result
        tvDiscountRate.text = resources.getString(
            R.string.discount_rate,
            WaysOfDecision.calculateEquivalentDiscountRate(interestRate).round(2).toString()
        )
        tvMainResult.visibility = View.VISIBLE
        val chartData = ChartData.tabulateFutureValue(pastValue, interestRate, term, inflation)
        buildChart(chartData)
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

        if (etPastValue.text.isNotEmpty()) {
            pastValue = etPastValue.text.toString().toDouble()
        } else if (unknownVariable != Parameter.PV) {
            etPastValue.error = resources.getString(R.string.empty_field)
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

    private fun buildChart(data: Map<Int, Double>) {

        val linearChart = AnyChart.line()
        linearChart.animation(true)
        linearChart.padding(10.0, 20.0, 5.0, 20.0)

        linearChart.crosshair().enabled(true)
        linearChart.crosshair()
            .yLabel(true)
        linearChart.tooltip().positionMode(TooltipPositionMode.POINT)

        linearChart.title("FV(t)")
        linearChart.yAxis(0).title("Future value")
        linearChart.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)

        val dataEntries = mutableListOf<DataEntry>()
        data.forEach{ (index, value) ->
            run {
                dataEntries.add(ValueDataEntry(index, value))
            }
        }

        val set = Set.instantiate()
        set.data(dataEntries)
        val series1Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        val series1: Line = linearChart.line(series1Mapping)
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        linearChart.legend().enabled(true)
        linearChart.legend().fontSize(13.0)
        linearChart.legend().padding(0.0, 0.0, 10.0, 0.0)

        chart.setChart(linearChart)
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


// My extension functions
fun EditText.clearError() {
    error = null
}

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()