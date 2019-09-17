package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.avjindersinghsekhon.minimaltodo.databinding.PaymentLayoutBinding
import com.google.android.material.snackbar.Snackbar
import com.sumup.merchant.Models.TransactionInfo
import com.sumup.merchant.api.SumUpAPI
import com.sumup.merchant.api.SumUpAPI.Response.ResultCode.SUCCESSFUL
import com.sumup.merchant.api.SumUpLogin
import com.sumup.merchant.api.SumUpPayment
import java.math.BigDecimal
import java.util.*

/**
 * Created on 2019-09-06, 19:31.
 * @author Akram Shokri
 */
class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: PaymentLayoutBinding
    private val REQUEST_CODE_SUMMUP_PAYMENT = 2
    private val REQUEST_CODE_SUMMUP_LOGIN = 1
    private val mAffiliateKey = "0fe74f65-093a-41c0-9e6b-281e8a9f8514"//BuildConfig.sumupAffiliateKey
    val TAG = "SumUp-merka"

    companion object {
        val TRANSACTION_CODE = "sumUp_transaction_code"
        val MERCHANT_CODE = "sumUp_merchant_code"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_layout)

        setListener()
    }

    private fun initTheme() {
        val theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
        if (theme == MainActivity.DARKTHEME) {
            setTheme(R.style.CustomStyle_DarkTheme)
        } else {
            setTheme(R.style.CustomStyle_LightTheme)
        }
    }

    private fun setListener() {
        binding.paymentBtn.setOnClickListener {
            val msg = validateInputs()
            if (msg.isBlank()) {
                binding.paymentBtn.isActivated = false
                checkLogin()
            } else {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
        }

        addTextChangeListener(binding.price)
        addTextChangeListener(binding.tipping)
    }

    private fun validateInputs(): String {
        if (binding.price.text.toString().isBlank()
                || binding.productTitle.text.toString().isBlank()) {
            return return getString(R.string.fill_required_fields)
        }

        if(!binding.price.text.toString().isNullOrBlank() &&
                readTextViewDoubleValue(binding.price) < 1.0 ){
            return getString(R.string.invalid_price_value)
        }
        return ""
    }

    private fun addTextChangeListener(input: EditText){
        input.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var total = readTextViewDoubleValue(binding.price)
                total += readTextViewDoubleValue(binding.tipping)
                binding.totalAmount.text = total.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }


        })
    }

    private fun readTextViewDoubleValue(input: TextView): Double {
        var value = input.text.toString()
        if(!value.isNullOrBlank()){
            return value.toDouble()
        }
        return 0.0
    }

    private fun checkLogin() {
        if (!SumUpAPI.isLoggedIn()) {
            val builder = SumUpLogin.builder(mAffiliateKey)
            SumUpAPI.openLoginActivity(this@PaymentActivity, builder.build(), REQUEST_CODE_SUMMUP_LOGIN)
        }
    }

    private fun sumUpCreatePayment() {
        val payment = SumUpPayment.builder()
                .total(BigDecimal.valueOf(readTextViewDoubleValue(binding.price)))
                .currency(SumUpPayment.Currency.EUR)
                .tip(BigDecimal.valueOf(readTextViewDoubleValue(binding.tipping)))
                .title(binding.productTitle.text.toString())
                .receiptEmail("akram.shokri@mail.com")
                .receiptSMS("+989051726050")
                .foreignTransactionId(UUID.randomUUID().toString())
                .skipSuccessScreen()
                .build()

        if (SumUpAPI.isLoggedIn()) {
            SumUpAPI.checkout(this@PaymentActivity, payment, REQUEST_CODE_SUMMUP_PAYMENT)
        } else {
            Log.e(TAG, "not logged in ! ")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SUMMUP_LOGIN && data != null) {
            val resCode = data.extras!!.getInt(SumUpAPI.Response.RESULT_CODE)
            val msg = data.extras!!.getString(SumUpAPI.Response.MESSAGE)

            Log.d(TAG, "result code:$resCode")
            Log.d(TAG, "result msg:" + msg!!)
            if(resCode == SUCCESSFUL){
                sumUpCreatePayment()
            } else{
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }

        } else if (requestCode == REQUEST_CODE_SUMMUP_PAYMENT && data != null) {
            val resCode = data.extras!!.getInt(SumUpAPI.Response.RESULT_CODE)
            val transactionInfo = data.extras!!.getParcelable<TransactionInfo>(SumUpAPI.Response.TX_INFO)
            if (resCode == SUCCESSFUL) {
                val intent = Intent(this, ReceiptActivity::class.java)
                intent.putExtra(TRANSACTION_CODE, transactionInfo.transactionCode)
                intent.putExtra(MERCHANT_CODE, transactionInfo.merchantCode)
                startActivity(intent)
                finish()
            } else {
                val msg = data.extras!!.getString(SumUpAPI.Response.MESSAGE)
                Snackbar.make(binding.root, msg!!, Snackbar.LENGTH_LONG).show()

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}