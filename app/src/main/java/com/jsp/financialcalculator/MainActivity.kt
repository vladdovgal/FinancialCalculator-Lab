package com.jsp.financialcalculator

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeData()
    }

    private fun observeData() {
        // todo data data processing

        // todo build chart
        setUnknownValueSwitcher()
        observeIfInflation()
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
                }
                rbPastValue -> {
                    etFutureValue.isEnabled = true
                    etPastValue.isEnabled = false
                    etTerm.isEnabled = true
                }
                rbTerm -> {
                    etFutureValue.isEnabled = true
                    etPastValue.isEnabled = true
                    etTerm.isEnabled = false
                }
            }
        }
}