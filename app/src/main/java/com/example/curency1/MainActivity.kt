package com.example.curency1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
   private lateinit var inputText: EditText
   private lateinit var result: TextView
   private lateinit var unit1: TextView
   private lateinit var unit2: TextView
   private lateinit var spinner1: Spinner
   private lateinit var spinner2: Spinner
   private lateinit var rateText: TextView

   private val currencies= arrayOf("United States - Dollar", "Europan - Euro", "Pound - Sterling", "Janpan - Yen", "Vietnam - Dong")
   private val rates = mapOf(
       "United States - Dollar" to 1.00,
       "Europan - Euro" to 0.93,
       "Pound - Sterling" to 0.77,
       "Janpan - Yen" to 150.59,
       "Vietnam - Dong" to 25560.00
   )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        inputText = findViewById(R.id.inputText)
        result = findViewById(R.id.result)
        unit1 = findViewById(R.id.unit1)
        unit2 = findViewById(R.id.unit2)
        spinner1 = findViewById(R.id.spinner_1)
        spinner2 = findViewById(R.id.spinner_2)
        rateText=findViewById(R.id.rate)

        // Thiết lập Adapter cho Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        spinner1.adapter=adapter
        spinner2.adapter=adapter

        // Đặt giá trị mặc định: USD & VND
        spinner1.setSelection(currencies.indexOf("United States - Dollar"))
        spinner2.setSelection(currencies.indexOf("Vietnam - Dong"))

        val unitChangeListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateUnits()
                updateExchangeRate()
                convertCurrency()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                TODO("Not yet implemented")
            }
        }

        spinner1.onItemSelectedListener = unitChangeListener
        spinner2.onItemSelectedListener = unitChangeListener

        inputText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convertCurrency()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        // xóa giá trị 0 mặc định
        inputText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputText.text.toString() == "0") {
                inputText.setText("")
            }
        }
        // Kích hoạt tự động đẩy UI khi bàn phím xuất hiện
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(0, 0, 0, imeInsets.bottom)
            insets
        }

    }
    private fun updateUnits() {
        val selectedCurrency1 = spinner1.selectedItem.toString()
        val selectedCurrency2 = spinner2.selectedItem.toString()
        unit1.text = getSymbolForCurrency(selectedCurrency1)
        unit2.text = getSymbolForCurrency(selectedCurrency2)

    }

    private fun getSymbolForCurrency(currency:String):String {
        return when (currency) {
            "United States - Dollar" -> "$"
            "Europan - Euro" -> "€"
            "Pound - Sterling" -> "£"
            "Janpan - Yen" -> "¥"
            "Vietnam - Dong" -> "₫"
            else -> ""
        }
    }
    private val currencyMap = mapOf(
        "United States - Dollar" to "USD",
        "Europan - Euro" to "EUR",
        "Pound - Sterling" to "GBP",
        "Janpan - Yen" to "JPY",
        "Vietnam - Dong" to "VND"
    )
    // Tỷ giá theo mã tiền tệ viết tắt
    private val rates1 = mapOf(
        "USD" to 1.00,
        "EUR" to 0.93,
        "GBP" to 0.77,
        "JPY" to 150.59,
        "VND" to 25560.00
    )

    // Cập nhật tỷ giá giữa hai đơn vị tiền tệ
    @SuppressLint("SetTextI18n")
    private fun updateExchangeRate() {
        val selectedCurrency1 = spinner1.selectedItem.toString()
        val selectedCurrency2 = spinner2.selectedItem.toString()

        val currencyCode1 = currencyMap[selectedCurrency1] ?: "USD"
        val currencyCode2 = currencyMap[selectedCurrency2] ?: "VND"

        val rate1 = rates1[currencyCode1] ?: 1.0
        val rate2 = rates1[currencyCode2] ?: 1.0
        val exchangeRate = rate2 / rate1

        // Định dạng số với dấu chấm
        val symbols = DecimalFormatSymbols(Locale.US) // chọn locale dùng dấu chấm
        symbols.decimalSeparator = '.'  // đặt dấu phân cách thập phân là dấu chấm

        val decimalFormat = DecimalFormat("#,###.00", symbols)
        rateText.text ="1 $currencyCode1 = ${decimalFormat.format(exchangeRate)} $currencyCode2"
//        rateText.text = "1 $currencyCode1 = %.2f $currencyCode2".format(exchangeRate)

    }




    @SuppressLint("DefaultLocale")
    private fun convertCurrency() {
        val inputValue = inputText.text.toString().toDoubleOrNull() ?: 0.0
        val selectedCurrency1 = spinner1.selectedItem.toString()
        val selectedCurrency2 = spinner2.selectedItem.toString()
        val rate1 = rates[selectedCurrency1] ?: 1.0
        val rate2 = rates[selectedCurrency2] ?: 1.0
        val convertedValue = inputValue * (rate2 / rate1)

        // định dạng số với dấu chấm
        val symbols = DecimalFormatSymbols(Locale.US) // chọn locale dùng dấu chấm
        symbols.decimalSeparator = '.'

        val decimalFormat = DecimalFormat("#,###.##", symbols)
        result.text = decimalFormat.format(convertedValue)

    }

}