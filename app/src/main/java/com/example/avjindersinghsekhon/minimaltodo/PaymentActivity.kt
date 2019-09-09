package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.avjindersinghsekhon.minimaltodo.databinding.PaymentLayoutBinding
import com.google.android.material.snackbar.Snackbar
import com.sumup.merchant.Models.TransactionInfo
import com.sumup.merchant.api.SumUpAPI
import com.sumup.merchant.api.SumUpAPI.Response.ResultCode.SUCCESSFUL
import com.sumup.merchant.api.SumUpAPI.Response.TX_CODE
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
    private val mAffiliateKey = "0fe74f65-093a-41c0-9e6b-281e8a9f8514"

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_layout)

        checkLogin()
        setClickListener()
    }

    private fun initTheme() {
        val theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
        if (theme == MainActivity.DARKTHEME) {
            setTheme(R.style.CustomStyle_DarkTheme)
        } else {
            setTheme(R.style.CustomStyle_LightTheme)
        }
    }

    private fun setClickListener() {
        binding.paymentBtn.setOnClickListener {
            if (validateInputs()) {
                binding.paymentBtn.isActivated = false

                sumUpCreatePayment()
            } else{
                Snackbar.make(binding.root, R.string.fill_required_fields, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if(binding.price.text.toString().isBlank()
                || binding.productTitle.text.toString().isBlank()) {
            return false
        }
        return true
    }

    private fun checkLogin() {
        if (!SumUpAPI.isLoggedIn()) {
            val builder = SumUpLogin.builder(mAffiliateKey)
            SumUpAPI.openLoginActivity(this@PaymentActivity, builder.build(), REQUEST_CODE_SUMMUP_LOGIN)
        }
    }

    private fun sumUpCreatePayment() {
        var total = binding.price.text.toString().toDouble()
        binding.totalAmount.text = total.toString()
        total += binding.tipping.text.toString().toDouble() * total
        val payment = SumUpPayment.builder()
                .total(BigDecimal.valueOf(total))
                .currency(SumUpPayment.Currency.EUR)
                .tip(BigDecimal.valueOf(binding.tipping.text.toString().toDouble()))
                .title(binding.productTitle.text.toString())
                .receiptEmail("akram.shokri@mail.com")
                .receiptSMS("+989051726050")
                .foreignTransactionId(UUID.randomUUID().toString())
                .skipSuccessScreen()
                .build()

        if (SumUpAPI.isLoggedIn()) {
            SumUpAPI.checkout(this@PaymentActivity, payment, REQUEST_CODE_SUMMUP_PAYMENT)
        } else {
            Log.e(MainActivity.TAG, "not logged in ! ")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SUMMUP_LOGIN && data != null) {
            val resCode = data.extras!!.getInt(SumUpAPI.Response.RESULT_CODE)
            val msg = data.extras!!.getString(SumUpAPI.Response.MESSAGE)
            //SumUpAPI.Response.ResultCode.
            Log.d(MainActivity.TAG, "result code:$resCode")
            Log.d(MainActivity.TAG, "result msg:" + msg!!)

        } else if (requestCode == REQUEST_CODE_SUMMUP_PAYMENT && data != null) {
            val resCode = data.extras!!.getInt(SumUpAPI.Response.RESULT_CODE)
            val transactionId = data.extras!!.getString(SumUpAPI.Param.FOREIGN_TRANSACTION_ID)
            Log.d(MainActivity.TAG, "result code:$resCode")
            Log.d(MainActivity.TAG, "transactionId:" + transactionId!!)
            Log.d(MainActivity.TAG, "msg:" + data.extras!!.getString(SumUpAPI.Response.MESSAGE)!!)
            val transactionInfo = data.extras!!.getParcelable<TransactionInfo>(SumUpAPI.Response.TX_INFO)
            val isReceiptSent = data.extras!!.getBoolean(SumUpAPI.Response.RECEIPT_SENT)
            if(resCode == SUCCESSFUL) {
                val intent = Intent(this, ReceiptActivity::class.java)
                intent.putExtra(TX_CODE, transactionInfo.transactionCode)
                startActivity(intent)
            }
            Log.d(MainActivity.TAG, "isReceiptSent:$isReceiptSent")
        }
    }
}