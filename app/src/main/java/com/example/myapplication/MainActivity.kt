package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var fromAmount: EditText
    private lateinit var toAmount: EditText
    private lateinit var fromCurrency: Spinner
    private lateinit var toCurrency: Spinner

    private val exchangeRates = mapOf(
        "Dollar" to 1.0,
        "EUR" to 0.94,
        "GBP" to 0.83,
        "JPY" to 149.5,
        "VND" to 24500.0
    )
    private var isUserInput = true
    private var isUpdatingFromAmount = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        fromAmount = findViewById(R.id.fromAmount)
        toAmount = findViewById(R.id.toAmount)
        fromCurrency = findViewById(R.id.fromCurrency)
        toCurrency = findViewById(R.id.toCurrency)

        setupSpinners()
        setupEditTexts()
    }
    private fun setupSpinners() {
        val currencies = exchangeRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromCurrency.adapter = adapter
        toCurrency.adapter = adapter

        // Mặc định USD -> VND
        fromCurrency.setSelection(currencies.indexOf("Dollar"))
        toCurrency.setSelection(currencies.indexOf("VND"))

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fromCurrency.onItemSelectedListener = spinnerListener
        toCurrency.onItemSelectedListener = spinnerListener
    }

    private fun setupEditTexts() {
        val fromTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUserInput && isUpdatingFromAmount) {
                    updateToAmount()
                }
            }
        }

        val toTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUserInput && !isUpdatingFromAmount) {
                    updateFromAmount()
                }
            }
        }

        fromAmount.addTextChangedListener(fromTextWatcher)
        toAmount.addTextChangedListener(toTextWatcher)

        // Xử lý khi focus thay đổi
        fromAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isUpdatingFromAmount = true
            }
        }

        toAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isUpdatingFromAmount = false
            }
        }

        // Mặc định giá trị ban đầu
        fromAmount.setText("1")
    }

    private fun updateFromAmount() {
        try {
            val fromCurrencyCode = fromCurrency.selectedItem as String
            val toCurrencyCode = toCurrency.selectedItem as String

            val toValue = toAmount.text.toString().toDoubleOrNull() ?: 0.0

            val fromRate = exchangeRates[fromCurrencyCode] ?: 1.0
            val toRate = exchangeRates[toCurrencyCode] ?: 1.0
            val conversionRate = fromRate / toRate

            val result = toValue * conversionRate

            isUserInput = false
            fromAmount.setText(String.format("%.2f", result))
            isUserInput = true

        } catch (e: Exception) {
            isUserInput = false
            fromAmount.setText("0")
            isUserInput = true
        }
    }

    private fun updateToAmount() {
        try {
            val fromCurrencyCode = fromCurrency.selectedItem as String
            val toCurrencyCode = toCurrency.selectedItem as String

            val fromValue = fromAmount.text.toString().toDoubleOrNull() ?: 0.0

            val fromRate = exchangeRates[fromCurrencyCode] ?: 1.0
            val toRate = exchangeRates[toCurrencyCode] ?: 1.0
            val conversionRate = toRate / fromRate

            val result = fromValue * conversionRate

            isUserInput = false
            toAmount.setText(String.format("%.2f", result))
            isUserInput = true

        } catch (e: Exception) {
            isUserInput = false
            toAmount.setText("0")
            isUserInput = true
        }
    }

    private fun updateConversion() {
        if (isUpdatingFromAmount) {
            updateToAmount()
        } else {
            updateFromAmount()
        }
    }
}