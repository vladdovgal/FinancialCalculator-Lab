package com.jsp.financialcalculator.utils

import android.app.Activity
import android.content.Context
import android.graphics.DashPathEffect
import android.view.View
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.jsp.financialcalculator.R
import com.jsp.financialcalculator.view.MainActivity.Companion.chargesPerYear
import com.jsp.financialcalculator.view.round

class ChartUtils(val activity: Activity) {
    fun tabulateFutureValue(pastValue : Double, interestRate : Double, term : Int, inflation : Double) : Map<Int, Double>{
        val tabulatedFun = mutableMapOf<Int, Double>()
        for(i in 0..term) {
            tabulatedFun[i] = WaysOfDecision.findFutureValue(
                pastValue = pastValue,
                interestRate = interestRate,
                term = i,
                inflation = inflation,
                chargesPerYear = chargesPerYear
            )
        }

        tabulatedFun.forEach { (index, value) ->
            logI("Day $index : FV = ${value.round(5)}")
        }
        return tabulatedFun
    }
    fun buildLinearChart(data : Map<Int, Double>, context: Context) {
        val chart : LineChart = activity.findViewById(R.id.chart)
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true);

        val values = arrayListOf<Entry>()
        data.forEach { (day, value) ->
            values.add(Entry(day.toFloat(), value.toFloat()))
        }
        val set1: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "Future value")
            set1.setDrawIcons(false)
            set1.enableDashedLine(10f, 5f, 0f)
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.color = activity.resources.getColor(R.color.secondaryColor)
            set1.setCircleColor(activity.resources.getColor(R.color.secondaryColor))
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            set1.fillColor = activity.resources.getColor(R.color.primaryColor)
            chart.description.isEnabled = false
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val chartData = LineData(dataSets)
            chart.data = chartData
            chart.invalidate()
            chart.visibility = View.VISIBLE
        }
    }
}
